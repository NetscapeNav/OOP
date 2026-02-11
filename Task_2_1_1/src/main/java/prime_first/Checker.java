package prime_first;

public class Checker {
    public boolean has_composite(int[] array) {
        if (array.length == 0) return false;
        for (int i = 0; i < array.length; i++) {
            if (is_composite(array[i])) {
                return true;
            }
        }
        return false;
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
