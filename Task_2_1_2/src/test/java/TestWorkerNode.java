import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.example.worker.WorkerNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Timeout(value = 10, unit = TimeUnit.SECONDS)
public class TestWorkerNode {
    private static final int MSG_ACCEPTED = 1;
    private static final int MSG_HEARTBEAT = 2;
    private static final int MSG_RESULT = 3;
    private static final int MSG_CANCEL = 4;

    private Thread workerThread;

    @AfterEach
    public void tearDown() throws InterruptedException {
        if (workerThread != null) {
            workerThread.interrupt();
            workerThread.join(1500);
        }
    }

    @Test
    public void testWorkerWithOnlyPrimes() throws Exception {
        int port = startWorker();
        WorkerResponse response = sendTask(port, 10, 1, new int[]{2, 3, 5, 2147483647});

        Assertions.assertTrue(response.accepted);
        Assertions.assertFalse(response.hasComposite);
        Assertions.assertEquals(Boolean.FALSE, response.results.get(2147483647));
    }

    @Test
    public void testWorkerWithCompositeNumbers() throws Exception {
        int port = startWorker();
        WorkerResponse response = sendTask(port, 11, 1, new int[]{7, 11, 4, 9});

        Assertions.assertTrue(response.accepted);
        Assertions.assertTrue(response.hasComposite);
        Assertions.assertEquals(Boolean.TRUE, response.results.get(4));
        Assertions.assertFalse(response.results.containsKey(9));
    }

    @Test
    public void testWorkerEmptyTask() throws Exception {
        int port = startWorker();
        WorkerResponse response = sendTask(port, 12, 1, new int[]{});

        Assertions.assertTrue(response.accepted);
        Assertions.assertFalse(response.hasComposite);
        Assertions.assertTrue(response.results.isEmpty());
    }

    @Test
    public void testWorkerMainDefaultPortStartsAndStops() throws Exception {
        workerThread = new Thread(() -> WorkerNode.main(new String[]{}));
        workerThread.setDaemon(true);
        workerThread.start();
        Thread.sleep(600);

        workerThread.interrupt();
        workerThread.join(1500);
        Assertions.assertFalse(workerThread.isAlive());
    }

    private int startWorker() throws Exception {
        int port = findFreePort();
        workerThread = new Thread(() -> WorkerNode.main(new String[]{String.valueOf(port)}));
        workerThread.setDaemon(true);
        workerThread.start();
        Thread.sleep(600);
        return port;
    }

    private int findFreePort() throws Exception {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private WorkerResponse sendTask(int port, int taskID, int attemptID,
                                    int[] numbers) throws Exception {
        try (Socket socket = new Socket("127.0.0.1", port);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             DataInputStream input = new DataInputStream(socket.getInputStream())) {
            output.writeInt(taskID);
            output.writeInt(attemptID);
            output.writeInt(numbers.length);
            for (int number : numbers) {
                output.writeInt(number);
            }
            output.flush();

            WorkerResponse response = new WorkerResponse();

            while (true) {
                int messageType = input.readInt();
                int responseTaskID = input.readInt();
                int responseAttemptID = input.readInt();

                Assertions.assertEquals(taskID, responseTaskID);
                Assertions.assertEquals(attemptID, responseAttemptID);

                if (messageType == MSG_ACCEPTED) {
                    response.accepted = true;
                } else if (messageType == MSG_HEARTBEAT) {
                    response.heartbeatCount++;
                } else if (messageType == MSG_RESULT) {
                    response.hasComposite = input.readBoolean();
                    int resultCount = input.readInt();

                    for (int i = 0; i < resultCount; i++) {
                        int number = input.readInt();
                        boolean isComposite = input.readBoolean();
                        response.results.put(number, isComposite);
                    }

                    return response;
                } else {
                    Assertions.fail("Unknown message type: " + messageType);
                }
            }
        }
    }

    @Test
    public void testWorkerStopsAfterCancelMessage() throws Exception {
        int port = startWorker();

        int taskID = 20;
        int attemptID = 1;
        int[] numbers = new int[]{
                2147483647, 2147483629, 2147483587, 2147483579,
                2147483563, 2147483549, 2147483543, 2147483497,
                2147483489, 2147483477, 2147483423, 2147483399
        };

        boolean accepted = false;

        try (Socket socket = new Socket("127.0.0.1", port);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             DataInputStream input = new DataInputStream(socket.getInputStream())) {

            socket.setSoTimeout(3000);

            output.writeInt(taskID);
            output.writeInt(attemptID);
            output.writeInt(numbers.length);
            for (int number : numbers) {
                output.writeInt(number);
            }

            output.writeInt(MSG_CANCEL);
            output.writeInt(taskID);
            output.writeInt(attemptID);
            output.flush();

            while (true) {
                int messageType = input.readInt();
                int responseTaskID = input.readInt();
                int responseAttemptID = input.readInt();

                Assertions.assertEquals(taskID, responseTaskID);
                Assertions.assertEquals(attemptID, responseAttemptID);

                if (messageType == MSG_ACCEPTED) {
                    accepted = true;
                } else if (messageType == MSG_HEARTBEAT) {
                    continue;
                } else if (messageType == MSG_RESULT) {
                    Assertions.fail("Worker must not send MSG_RESULT after MSG_CANCEL");
                } else {
                    Assertions.fail("Unknown message type: " + messageType);
                }
            }
        } catch (EOFException | SocketException e) {
            Assertions.assertTrue(accepted);
        }
    }

    private static class WorkerResponse {
        boolean accepted;
        int heartbeatCount;
        boolean hasComposite;
        Map<Integer, Boolean> results = new HashMap<>();
    }
}
