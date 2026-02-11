package prime_first;

import java.util.Arrays;
import java.util.List;

public class ParallelsChecker {
    public boolean has_composite(int[] array) {
        return Arrays.stream(array).parallel()
                .anyMatch(this::is_composite);
    }

    private boolean is_composite(int num) {
        if (num < 2) {
            return false;
        }
        for (int i = 2; i*i <= num; i++) {
            if (num % i == 0) {
                return true;
            }
        }
        return false;
    }
}
