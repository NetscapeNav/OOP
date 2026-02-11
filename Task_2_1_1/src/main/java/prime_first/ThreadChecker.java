package prime_first;

public class ThreadChecker {
    public boolean has_composite(int[] array, int threadCount) {
        if (array.length == 0) return false;

        Check[] checks = new Check[threadCount];

        for (int i = 0; i < threadCount; i++) {
            int start = array.length * i / threadCount;
            int end = array.length * (i + 1) / threadCount;

            checks[i] = new Check(array, start, end);
            checks[i].start();
        }

        for (int i = 0; i < threadCount; i++) {
            try {
                checks[i].join();
                if (checks[i].getResult()) {
                    return true;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return false;
    }

    private static class Check extends Thread {
        private int start;
        private int end;
        private int[] array;
        private boolean result = false;

        public Check(int[] array, int start, int end) {
            this.start = start;
            this.end = end;
            this.array = array;
        }

        public boolean getResult() {
            return result;
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

        @Override
        public void run() {
            for (int i = start; i < end; i++) {
                if (is_composite(array[i])) {
                    result = true;
                    return;
                }
            }
        }
    }
}
