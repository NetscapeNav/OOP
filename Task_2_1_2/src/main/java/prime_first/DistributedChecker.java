package prime_first;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class DistributedChecker implements PrimeFinder {
    private final String[] IPsockets = {"netscape-localhost", "netscape-localhost", "netscape-localhost"};
    private final int[] port = {8080, 8081, 8082};

    public DistributedChecker() {
    }

    @Override
    public boolean hasComposite(int[] array) {
        if (array == null || array.length == 0) {
            return false;
        }

        int portCount = port.length;
        Thread[] threads = new Thread[portCount];
        AtomicBoolean hasComposite = new AtomicBoolean(false);

        for (int i = 0; i < portCount; i++) {
            final int taskId = i;
            final String ip = IPsockets[taskId];
            final int currentPort = port[taskId];

            threads[i] = new Thread(() -> {
                int start = array.length / portCount * taskId;
                int end = (taskId == portCount - 1) ? array.length : array.length / portCount * (taskId + 1);

                try (Socket socket = new Socket(ip, currentPort);
                     DataInputStream input = new DataInputStream(socket.getInputStream());
                     DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
                    socket.setSoTimeout(7500);

                    output.writeInt(end - start);
                    for (int j = start; j < end; j++) {
                        output.writeInt(array[j]);
                    }
                    output.flush();

                    if (input.readBoolean()) {
                        hasComposite.set(true);
                    }
                } catch (Exception e) {
                    for (int j = start; j < end; j++) {
                        if (!hasComposite.get() && PrimeUtils.isComposite(array[j])) {
                            hasComposite.set(true);
                            break;
                        }
                    }
                }
            });

            threads[i].start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return hasComposite.get();
    }
}
