package prime_first;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    public void startServer(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port);) {
            serverSocket.setSoTimeout(500);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    Thread clientThread = new Thread(() -> handleClient(clientSocket));
                    clientThread.setDaemon(true);
                    clientThread.start();
                } catch (SocketTimeoutException e) {
                    if (Thread.currentThread().isInterrupted()) {
                        break;
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

            Map<Integer, Boolean> results = new LinkedHashMap<>();
            boolean hasComposite = false;

            for (int num : numbers) {
                Boolean res = cache.computeIfAbsent(num, PrimeUtils::isComposite);
                results.put(num, res);
                if (res) {
                    hasComposite = true;
                }
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
            e.printStackTrace();
        } finally {
            finished.set(true);

            if (heartbeatThread != null) {
                heartbeatThread.interrupt();
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
}
