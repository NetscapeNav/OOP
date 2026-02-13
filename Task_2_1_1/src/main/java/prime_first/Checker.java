package prime_first;

public class Checker implements PrimeFinder {
    @Override
    public boolean has_composite(int[] array) {
        if (array.length == 0) return false;
        for (int i = 0; i < array.length; i++) {
            if (PrimeUtils.isComposite(array[i])) {
                return true;
            }
        }
        return false;
    }
}
