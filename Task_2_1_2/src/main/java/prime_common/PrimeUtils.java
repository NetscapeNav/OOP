package prime_common;

public class PrimeUtils {
    public static boolean isComposite(int num) {
        if (num <= 2) {
            return false;
        }

        if (num % 2 == 0) {
            return true;
        }

        for (int i = 3; i <= num / i; i += 2) {
            if (num % i == 0) {
                return true;
            }
        }
        return false;
    }
}
