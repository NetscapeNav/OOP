import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.example.common.PrimeUtils;

import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicInteger;

public class TestPrimeUtils {
    @Test
    public void testPrimeNumbers() {
        Assertions.assertFalse(PrimeUtils.isComposite(2));
        Assertions.assertFalse(PrimeUtils.isComposite(3));
        Assertions.assertFalse(PrimeUtils.isComposite(2147483647));
    }

    @Test
    public void testCompositeNumbers() {
        Assertions.assertTrue(PrimeUtils.isComposite(4));
        Assertions.assertTrue(PrimeUtils.isComposite(9));
        Assertions.assertTrue(PrimeUtils.isComposite(2147483646));
    }

    @Test
    public void testCancellationBeforeCalculation() {
        Assertions.assertThrows(CancellationException.class,
                () -> PrimeUtils.isComposite(2147483647, () -> true));
    }

    @Test
    public void testCancellationDuringCalculation() {
        AtomicInteger checks = new AtomicInteger(0);

        Assertions.assertThrows(CancellationException.class,
                () -> PrimeUtils.isComposite(2147483647, () -> checks.incrementAndGet() > 1));
    }
}
