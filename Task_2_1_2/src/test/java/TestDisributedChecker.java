import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import prime_first.DistributedChecker;
import prime_first.WorkerNode;

import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Timeout(value = 20, unit = TimeUnit.SECONDS)
public class TestDisributedChecker {
    private final List<Thread> workers = new ArrayList<>();

    @AfterEach
    public void tearDown() throws InterruptedException {
        for (Thread worker : workers) {
            worker.interrupt();
        }
        for (Thread worker : workers) {
            worker.join(1500);
        }
    }

    @Test
    public void testPrimesReturnFalse() throws Exception {
        int[] ports = startWorkers(3);
        DistributedChecker checker = new DistributedChecker(localIps(3), ports, 2);

        Assertions.assertFalse(checker.hasComposite(new int[]{2, 3, 5, 7, 11, 13, 17, 19}));
    }

    @Test
    public void testCompositeReturnsTrue() throws Exception {
        int[] ports = startWorkers(3);
        DistributedChecker checker = new DistributedChecker(localIps(3), ports, 2);

        Assertions.assertTrue(checker.hasComposite(new int[]{2, 3, 5, 7, 9, 11, 13}));
    }

    @Test
    public void testLargePrimeArrayReturnsFalse() throws Exception {
        int[] ports = startWorkers(3);
        DistributedChecker checker = new DistributedChecker(localIps(3), ports, 3);

        int[] array3 = new int[]{
                2147483647, 2147483629, 2147483587, 2147483579,
                2147483563, 2147483549, 2147483543, 2147483497,
                2147483489, 2147483477, 2147483423, 2147483399
        };

        Assertions.assertFalse(checker.hasComposite(array3));
    }

    @Test
    public void testNullAndEmptyArraysReturnFalse() throws Exception {
        int[] ports = startWorkers(1);
        DistributedChecker checker = new DistributedChecker(localIps(1), ports, 2);

        Assertions.assertFalse(checker.hasComposite(null));
        Assertions.assertFalse(checker.hasComposite(new int[]{}));
    }

    @Test
    public void testGlobalCacheReusesPrimeAndCompositeResults() throws Exception {
        int[] ports = startWorkers(1);
        DistributedChecker checker = new DistributedChecker(localIps(1), ports, 2);

        Assertions.assertFalse(checker.hasComposite(new int[]{2147483647, 2147483629}));
        Assertions.assertFalse(checker.hasComposite(new int[]{2147483647, 2147483629}));

        Assertions.assertTrue(checker.hasComposite(new int[]{9}));
        Assertions.assertTrue(checker.hasComposite(new int[]{9}));
    }

    @Test
    public void testUnavailableWorkerDoesNotLoseTask() throws Exception {
        int livePort = startWorkers(1)[0];
        int deadPort = findFreePort();
        DistributedChecker checker = new DistributedChecker(
                new String[]{"127.0.0.1", "127.0.0.1"},
                new int[]{deadPort, livePort},
                1
        );

        Assertions.assertFalse(checker.hasComposite(new int[]{2, 3, 5, 7, 11}));
    }

    @Test
    public void testWaitsUntilWorkerBecomesAvailable() throws Exception {
        int delayedPort = findFreePort();
        DistributedChecker checker = new DistributedChecker(
                new String[]{"127.0.0.1"},
                new int[]{delayedPort},
                1
        );

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = executor.submit(() -> checker.hasComposite(new int[]{2, 3, 4}));

        Thread.sleep(700);
        Assertions.assertFalse(future.isDone());

        startWorker(delayedPort);

        try {
            Assertions.assertTrue(future.get(8, TimeUnit.SECONDS));
        } finally {
            executor.shutdownNow();
        }
    }

    @Test
    public void testInvalidConstructorArguments() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new DistributedChecker(null, new int[]{8080}));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new DistributedChecker(new String[]{"127.0.0.1"}, new int[]{}));
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> new DistributedChecker(new String[]{"127.0.0.1"}, new int[]{8080}, 0));
    }

    private int[] startWorkers(int count) throws Exception {
        int[] ports = new int[count];
        for (int i = 0; i < count; i++) {
            ports[i] = findFreePort();
            startWorker(ports[i]);
        }
        Thread.sleep(600);
        return ports;
    }

    private void startWorker(int port) {
        Thread worker = new Thread(() -> WorkerNode.main(new String[]{String.valueOf(port)}));
        worker.setDaemon(true);
        worker.start();
        workers.add(worker);
    }

    private int findFreePort() throws Exception {
        try (ServerSocket socket = new ServerSocket(0)) {
            return socket.getLocalPort();
        }
    }

    private String[] localIps(int count) {
        String[] ips = new String[count];
        for (int i = 0; i < count; i++) {
            ips[i] = "127.0.0.1";
        }
        return ips;
    }
}
