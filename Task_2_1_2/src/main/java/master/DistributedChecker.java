package master;

import prime_common.DistributedProtocol;
import prime_common.PrimeFinder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DistributedChecker implements PrimeFinder {
    private final WorkerAddress[] workers;
    private final int chunkSize;
    private final Map<Integer, Boolean> globalCache = new ConcurrentHashMap<>();

    public DistributedChecker() {
        this(
                new String[]{"127.0.0.1", "127.0.0.1", "127.0.0.1"},
                new int[]{8080, 8081, 8082},
                DistributedProtocol.DEFAULT_CHUNK_SIZE
        );
    }

    public DistributedChecker(String[] ips, int[] ports) {
        this(ips, ports, DistributedProtocol.DEFAULT_CHUNK_SIZE);
    }

    public DistributedChecker(String[] ips, int[] ports, int chunkSize) {
        if (ips == null || ports == null || ips.length != ports.length || ips.length == 0) {
            throw new IllegalArgumentException("Workers are not specified correctly");
        }
        if (chunkSize <= 0) {
            throw new IllegalArgumentException("Chunk size must be positive");
        }

        this.workers = new WorkerAddress[ips.length];
        for (int i = 0; i < ips.length; i++) {
            this.workers[i] = new WorkerAddress(ips[i], ports[i]);
        }
        this.chunkSize = chunkSize;
    }

    @Override
    public boolean hasComposite(int[] array) {
        if (array == null || array.length == 0) {
            return false;
        }

        List<Integer> numbersToCheck = collectNumbersNotInCache(array);

        if (numbersToCheck == null) {
            return true;
        }

        if (numbersToCheck.isEmpty()) {
            return false;
        }

        Object completionLock = new Object();

        List<DistributedTask> tasks = createTasks(numbersToCheck);
        BlockingQueue<DistributedTask> pendingTasks = new LinkedBlockingQueue<>(tasks);

        AtomicInteger unfinishedTasks = new AtomicInteger(tasks.size());
        AtomicBoolean hasComposite = new AtomicBoolean(false);
        AtomicBoolean stop = new AtomicBoolean(false);

        List<Thread> dispatcherThreads = new ArrayList<>();

        for (WorkerAddress worker : workers) {
            Thread dispatcherThread = new Thread(() -> dispatchThreads(
                    worker,
                    completionLock,
                    pendingTasks,
                    unfinishedTasks,
                    hasComposite,
                    stop
            ));

            dispatcherThread.setDaemon(true);
            dispatcherThread.start();
            dispatcherThreads.add(dispatcherThread);
        }

        try {
            synchronized (completionLock) {
                while (!hasComposite.get() && unfinishedTasks.get() > 0) {
                    completionLock.wait();
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Distributed checking was interrupted", e);
        } finally {
            stop.set(true);
            for (Thread thread : dispatcherThreads) {
                thread.interrupt();
            }
            for (Thread thread : dispatcherThreads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Distributed checking was interrupted", e);
                }
            }
        }

        return hasComposite.get();
    }

    private void dispatchThreads(WorkerAddress worker, Object completionLock, BlockingQueue<DistributedTask> pendingTasks,
                                 AtomicInteger unfinishedTasks, AtomicBoolean hasComposite, AtomicBoolean stop) {
        while (!stop.get() && !Thread.currentThread().isInterrupted()) {
            if (unfinishedTasks.get() == 0) {
                return;
            }

            DistributedTask task;

            try {
                task = pendingTasks.poll(DistributedProtocol.RETRY_DELAY_MS, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }

            if (task == null) {
                continue;
            }

            int attemptID = task.startAttempt();

            try {
                WorkerResult result = sendTaskAndWaitResult(worker, task, attemptID);
                globalCache.putAll(result.results);
                task.markDone();
                int leftTasks = unfinishedTasks.decrementAndGet();

                if (result.hasComposite) {
                    hasComposite.set(true);
                    stop.set(true);
                }

                if (result.hasComposite || leftTasks == 0) {
                    synchronized (completionLock) {
                        completionLock.notifyAll();
                    }
                }
            } catch (IOException e) {
                if (!stop.get() && !task.isDone()) {
                    task.markPending();
                    pendingTasks.offer(task);
                    sleepBeforeRetry();
                }
            }
        }
    }

    private List<DistributedTask> createTasks(List<Integer> numbers) {
        List<DistributedTask> tasks = new ArrayList<>();
        int taskID = 0;

        for (int start = 0; start < numbers.size(); start += chunkSize) {
            int end = Math.min(start + chunkSize, numbers.size());
            int[] taskNumbers = new int[end - start];

            for (int i = start; i < end; i++) {
                taskNumbers[i - start] = numbers.get(i);
            }

            tasks.add(new DistributedTask(taskID, taskNumbers));
            taskID++;
        }
        
        return tasks;
    }

    private List<Integer> collectNumbersNotInCache(int[] array) {
        List<Integer> numbers = new ArrayList<>();
        Set<Integer> addedNumbers = new HashSet<>();
        for (int num : array) {
            Boolean cachedResult = globalCache.get(num);
            if (Boolean.TRUE.equals(cachedResult)) {
                return null;
            }
            if (cachedResult == null && addedNumbers.add(num)) {
                numbers.add(num);
            }
        }
        return numbers;
    }

    private WorkerResult sendTaskAndWaitResult(WorkerAddress worker, DistributedTask task,
                                               int attemptID) throws IOException {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(worker.ip, worker.port),
            DistributedProtocol.CONNECT_TIMEOUT_MS);
            socket.setSoTimeout(DistributedProtocol.HEARTBEAT_TIMEOUT_MS);
            try (DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                 DataInputStream input = new DataInputStream(socket.getInputStream())) {
                output.writeInt(task.taskID);
                output.writeInt(attemptID);
                output.writeInt(task.numbers.length);
                for (int num : task.numbers) {
                    output.writeInt(num);
                }
                output.flush();

                while (true) {
                    int messageType;
                    int responseTaskID;
                    int responseAttemptID;
                    try {
                        messageType = input.readInt();
                        responseTaskID = input.readInt();
                        responseAttemptID = input.readInt();
                    } catch (SocketTimeoutException e) {
                        throw new IOException("Worker timeout", e);
                    }

                    if (responseTaskID != task.taskID || responseAttemptID != attemptID) {
                        throw new IOException("Received response for another task");
                    }

                    if (messageType == DistributedProtocol.MSG_ACCEPTED) {
                        continue;
                    }

                    if (messageType == DistributedProtocol.MSG_HEARTBEAT) {
                        continue;
                    }

                    if (messageType == DistributedProtocol.MSG_RESULT) {
                        boolean hasComposite = input.readBoolean();
                        int resultCount = input.readInt();
                        Map<Integer, Boolean> results = new HashMap<>();

                        for (int i = 0; i < resultCount; i++) {
                            int number = input.readInt();
                            boolean isComposite = input.readBoolean();
                            results.put(number, isComposite);
                        }

                        return new WorkerResult(hasComposite, results);
                    }

                    throw new IOException("Unknown message type: " + messageType);
                }
            }

        }
    }

    private void sleepBeforeRetry() {
        try {
            Thread.sleep(DistributedProtocol.RETRY_DELAY_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    private enum TaskState {
        PENDING,
        IN_PROGRESS,
        DONE
    }

    private static class DistributedTask {
        final int taskID;
        final int[] numbers;
        
        private int attemptID;
        private TaskState state;

        DistributedTask(int taskID, int[] numbers) {
            this.taskID = taskID;
            this.numbers = numbers;
            this.attemptID = 0;
            this.state = TaskState.PENDING;
        }

        synchronized int startAttempt() {
            attemptID++;
            state = TaskState.IN_PROGRESS;
            return attemptID;
        }

        synchronized void markPending() {
            if (state != TaskState.DONE) {
                state = TaskState.PENDING;
            }
        }

        synchronized void markDone() {
            state = TaskState.DONE;
        }

        synchronized boolean isDone() {
            return state == TaskState.DONE;
        }
    }

    private static class WorkerAddress {
        final String ip;
        final int port;

        WorkerAddress(String ip, int port) {
            this.ip = ip;
            this.port = port;
        }
    }

    private static class WorkerResult {
        final boolean hasComposite;
        final Map<Integer, Boolean> results;

        WorkerResult(boolean hasComposite, Map<Integer, Boolean> results) {
            this.hasComposite = hasComposite;
            this.results = results;
        }
    }
}
