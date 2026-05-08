import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import prime_first.DistributedChecker;
import prime_first.WorkerNode;

public class TestDisributedChecker {
    private final int[] primes = {2, 3, 5, 7, 11, 13, 17, 19};
    private final int[] not_primes = {4, 6, 8, 9, 10, 12, 14, 16};
    private final int[] not_prime_once = {2, 3, 5, 7, 9, 11, 13, 17, 19};
    private final int[] largeMixed = {20319251, 6997901, 6997927, 6997937, 17858849,
            6997967, 6998009, 6998029, 6998039, 20165149,
            6998051, 6998053, 4};

    private Thread[] workers = new Thread[3];
    private final int[] testPorts = {8180, 8181, 8182};
    private final String[] testIps = {"127.0.0.1", "127.0.0.1", "127.0.0.1"};

    private DistributedChecker checker;

    @BeforeEach
    public void setUp() throws InterruptedException {
        for (int i = 0; i < 3; i++) {
            final int port = testPorts[i];
            workers[i] = new Thread(() -> WorkerNode.main(new String[]{String.valueOf(port)}));
            workers[i].start();
        }
        Thread.sleep(500);

        checker = new DistributedChecker(testIps, testPorts);
    }

    @AfterEach
    public void tearDown() {
        for (Thread worker : workers) {
            if (worker != null) worker.interrupt();
        }
    }

    @Test
    public void testPrimes() {
        Assertions.assertFalse(checker.hasComposite(primes));
    }

    @Test
    public void testNotPrimes() {
        Assertions.assertTrue(checker.hasComposite(not_primes));
    }

    @Test
    public void testNotPrimeOnce() {
        Assertions.assertTrue(checker.hasComposite(not_prime_once));
    }

    @Test
    public void testLargePrimes() {
        Assertions.assertTrue(checker.hasComposite(largeMixed));
    }
    
    @Test
    public void testEmptyAndNullArrays() {
        Assertions.assertFalse(checker.hasComposite(new int[]{}));
        Assertions.assertFalse(checker.hasComposite(null));
    }
}