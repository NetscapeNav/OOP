import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import prime_first.ThreadChecker;

public class TestThreadChecker {
    private final int[] primes = {2, 3, 5, 7, 11, 13, 17, 19};
    private final int[] not_primes = {4, 6, 8, 9, 10, 12, 14, 16};
    private final int[] not_prime_once = {2, 3, 5, 7, 9, 11, 13, 17, 19};
    private final int[] largeMixed = {20319251, 6997901, 6997927, 6997937, 17858849,
            6997967, 6998009, 6998029, 6998039, 20165149,
            6998051, 6998053, 4};

    @Test
    public void testPrimes() {
        ThreadChecker checker = new ThreadChecker();
        Assertions.assertFalse(checker.hasСomposite(primes, 3));
    }

    @Test
    public void testNotPrimes() {
        ThreadChecker checker = new ThreadChecker();
        Assertions.assertTrue(checker.hasСomposite(not_primes, 3));
    }

    @Test
    public void testNotPrimeOnce() {
        ThreadChecker checker = new ThreadChecker();
        Assertions.assertTrue(checker.hasСomposite(not_prime_once, 3));
    }

    @Test
    public void testLargePrimes() {
        ThreadChecker checker = new ThreadChecker();
        Assertions.assertTrue(checker.hasСomposite(largeMixed, 3));
    }
}
