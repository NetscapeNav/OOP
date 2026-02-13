import prime_first.Checker;
import prime_first.ParallelsChecker;
import prime_first.ThreadChecker;

public class TestPerfomance {
    public static void main(String[] args) {
        int[] data = generatePrimeArray(1000000);

        Checker checker = new Checker();
        long start = System.currentTimeMillis();
        checker.has_composite(data);
        long end = System.currentTimeMillis();
        System.out.println("Time required: " + (end - start) + " (Checker())");


        ThreadChecker threadChecker = new ThreadChecker();
        int cores = Runtime.getRuntime().availableProcessors();
        for (int i = 1; i <= cores + 4; i++) {
            start = System.currentTimeMillis();
            threadChecker.has_composite(data, i);
            end = System.currentTimeMillis();
            System.out.println("Time required: " + (end - start) + " (ThreadChecker(), " + i + " cores)");
        }


        ParallelsChecker parallelsChecker = new ParallelsChecker();
        start = System.currentTimeMillis();
        parallelsChecker.has_composite(data);
        end = System.currentTimeMillis();
        System.out.println("Time required: " + (end - start) + " (ParallelsChecker())");
    }

    private static int[] generatePrimeArray(int size) {
        int[] result = new int[size];
        int count = 0;
        int num = 20000000;
        while (count < size) {
            if (isPrime(num)) {
                result[count] = num;
                count++;
            }
            num++;
        }
        return result;
    }

    private static boolean isPrime(int num) {
        for (int i = 2; i*i <= num; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return true;
    }
}
