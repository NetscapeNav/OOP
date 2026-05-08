import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import prime_first.WorkerNode;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class TestWorkerNode {
    private Thread workerThread;
    private final int TEST_PORT = 8089;

    @BeforeEach
    public void setUp() throws InterruptedException {
        workerThread = new Thread(() -> {
            WorkerNode.main(new String[]{String.valueOf(TEST_PORT)});
        });
        workerThread.start();

        Thread.sleep(500);
    }

    @AfterEach
    public void tearDown() {
        if (workerThread != null) {
            workerThread.interrupt();
        }
    }

    @Test
    public void testWorkerWithOnlyPrimes() {
        try (Socket socket = new Socket("127.0.0.1", TEST_PORT);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             DataInputStream input = new DataInputStream(socket.getInputStream())) {

            // [2, 3, 5]
            output.writeInt(3); // length is 3
            output.writeInt(2);
            output.writeInt(3);
            output.writeInt(5);
            output.flush();

            boolean result = input.readBoolean();
            assertFalse(result, "Массив содержит только простые числа, ожидается false");

        } catch (Exception e) {
            fail("Сетевая ошибка: " + e.getMessage());
        }
    }

    @Test
    public void testWorkerWithCompositeNumbers() {
        try (Socket socket = new Socket("127.0.0.1", TEST_PORT);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             DataInputStream input = new DataInputStream(socket.getInputStream())) {

            // [7, 11, 4]
            output.writeInt(3); // length is 3
            output.writeInt(7);
            output.writeInt(11);
            output.writeInt(4);
            output.flush();

            boolean result = input.readBoolean();
            assertTrue(result, "Массив содержит составное число (4), ожидается true");

        } catch (Exception e) {
            fail("Сетевая ошибка: " + e.getMessage());
        }
    }
    
    @Test
    public void testWorkerEmptyArray() {
        try (Socket socket = new Socket("127.0.0.1", TEST_PORT);
             DataOutputStream output = new DataOutputStream(socket.getOutputStream());
             DataInputStream input = new DataInputStream(socket.getInputStream())) {

            // []
            output.writeInt(0); // length is 0
            output.flush();

            boolean result = input.readBoolean();
            assertFalse(result, "Пустой массив не содержит составных чисел, ожидается false");

        } catch (Exception e) {
            fail("Сетевая ошибка: " + e.getMessage());
        }
    }
    
    @Test
    public void testWorkerMainDefaultPort() throws InterruptedException {
        Thread defaultWorker = new Thread(() -> {
            WorkerNode.main(new String[]{});
        });
        defaultWorker.start();
        Thread.sleep(500);
        defaultWorker.interrupt();
    }
}
