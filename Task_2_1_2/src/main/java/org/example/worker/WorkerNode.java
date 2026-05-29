package org.example.worker;

import org.example.common.DistributedProtocol;
import org.example.common.PrimeUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicBoolean;

public class WorkerNode {
    private final Map<Integer, Boolean> cache = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        WorkerNode node = new WorkerNode();
        int port = 8080;
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        node.startServer(port);
    }

    private void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(500);
            while (!Thread.currentThread().isInterrupted()) {
                Socket clientSocket = null;
                try {
                    clientSocket = serverSocket.accept();
                    Socket socketForThread = clientSocket;
                    Thread clientThread = new Thread(() -> handleClient(socketForThread));
                    clientThread.setDaemon(true);
                    clientThread.start();
                    clientSocket = null;
                } catch (SocketTimeoutException e) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
                    }
                } catch (Exception e) {
                    if (clientSocket != null) {
                        try {
                            clientSocket.close();
                        } catch (Exception closeException) {
                            e.addSuppressed(closeException);
                        }
                    }

                    if (!Thread.currentThread().isInterrupted()) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            if (!Thread.currentThread().isInterrupted()) {
                e.printStackTrace();
            }
        }
    }

    private void handleClient(Socket socket) {
        Thread heartbeatThread = null;
        Thread cancelThread = null;
        AtomicBoolean finished = new AtomicBoolean(false);
        try (Socket clientSocket = socket;
             DataInputStream input = new DataInputStream(clientSocket.getInputStream());
             DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())) {

            int taskID = input.readInt();
            int attemptID = input.readInt();
            int length = input.readInt();

            int[] numbers = new int[length];
            for (int i = 0; i < length; i++) {
                numbers[i] = input.readInt();
            }

            sendMessage(output, DistributedProtocol.MSG_ACCEPTED, taskID, attemptID);

            heartbeatThread = new Thread(() -> sendHeartBeat(output, taskID, attemptID, finished));
            heartbeatThread.setDaemon(true);
            heartbeatThread.start();

            cancelThread = new Thread(() -> listenForCancel(input, taskID, attemptID, finished));
            cancelThread.setDaemon(true);
            cancelThread.start();

            Map<Integer, Boolean> results = new LinkedHashMap<>();
            boolean hasComposite = false;

            for (int num : numbers) {
                if (finished.get()) {
                    return;
                }

                Boolean res;
                try {
                    res = cache.computeIfAbsent(num, value -> PrimeUtils.isComposite(value, finished::get));
                } catch (CancellationException e) {
                    return;
                }

                results.put(num, res);

                if (res) {
                    hasComposite = true;
                    break;
                }
            }

            if (finished.get()) {
                return;
            }

            synchronized (output) {
                output.writeInt(DistributedProtocol.MSG_RESULT);
                output.writeInt(taskID);
                output.writeInt(attemptID);
                output.writeBoolean(hasComposite);
                output.writeInt(results.size());
                for (Map.Entry<Integer, Boolean> entry : results.entrySet()) {
                    output.writeInt(entry.getKey());
                    output.writeBoolean(entry.getValue());
                }
                output.flush();
            }

        } catch (Exception e) {
            if (!Thread.currentThread().isInterrupted()) {
                e.printStackTrace();
            }
        } finally {
            finished.set(true);

            if (heartbeatThread != null) {
                heartbeatThread.interrupt();
            }

            if (cancelThread != null) {
                cancelThread.interrupt();
            }
        }
    }

    private void sendHeartBeat(DataOutputStream output, int taskID, int attemptID, AtomicBoolean finished) {
        while (!finished.get() && !Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(DistributedProtocol.HEARTBEAT_INTERVAL_MS);
                if (!finished.get()) {
                    sendMessage(output, DistributedProtocol.MSG_HEARTBEAT, taskID, attemptID);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                finished.set(true);
                return;
            }
        }
    }

    private void sendMessage(DataOutputStream output, int msgAccepted, int taskID, int attemptID) throws Exception {
        synchronized (output) {
            output.writeInt(msgAccepted);
            output.writeInt(taskID);
            output.writeInt(attemptID);
            output.flush();
        }
    }

    private void listenForCancel(DataInputStream input, int taskID, int attemptID, AtomicBoolean finished) {
        while (!finished.get() && !Thread.currentThread().isInterrupted()) {
            try {
                int messageType = input.readInt();
                int receivedTaskID = input.readInt();
                int receivedAttemptID = input.readInt();

                if (messageType == DistributedProtocol.MSG_CANCEL
                        && receivedTaskID == taskID
                        && receivedAttemptID == attemptID) {
                    finished.set(true);
                    return;
                }
            } catch (Exception e) {
                finished.set(true);
                return;
            }
        }
    }
}
