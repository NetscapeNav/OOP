package prime_first;

public class PrimeUtils {
    public static boolean isComposite(int num) {
        if (num < 2) {
            return false;
        }
        for (int i = 2; i * i <= num; i++) {
            if (num % i == 0) {
                return true;
            }
        }
        return false;
    }
}
