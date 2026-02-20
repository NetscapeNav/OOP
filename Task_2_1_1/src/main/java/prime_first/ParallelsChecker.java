package prime_first;

import java.util.Arrays;
import java.util.List;

public class ParallelsChecker implements PrimeFinder {
    @Override
    public boolean hasComposite(int[] array) {
        return Arrays.stream(array).parallel()
                .anyMatch(PrimeUtils::isComposite);
    }
}
