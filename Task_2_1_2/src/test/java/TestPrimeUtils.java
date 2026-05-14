import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import prime_first.PrimeUtils;

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
}
