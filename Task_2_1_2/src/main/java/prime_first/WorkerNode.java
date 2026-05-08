package prime_first;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class WorkerNode {
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
            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     DataInputStream input = new DataInputStream(clientSocket.getInputStream());
                     DataOutputStream output = new DataOutputStream(clientSocket.getOutputStream())) {
                    int length = input.readInt();
                    boolean hasComposite = false;

                    for (int j = 0; j < length; j++) {
                        int num = input.readInt();
                        if (!hasComposite && PrimeUtils.isComposite(num)) {
                            hasComposite = true;
                        }
                    }

                    output.writeBoolean(hasComposite);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
