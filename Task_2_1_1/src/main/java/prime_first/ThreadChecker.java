package prime_first;

public class ThreadChecker implements PrimeFinder {
    private final int threadCount;

    public ThreadChecker() {
        this.threadCount = Runtime.getRuntime().availableProcessors();
    }

    public ThreadChecker(int threadCount) {
        this.threadCount = threadCount;
    }

    public boolean hasComposite(int[] array) {
        return hasСomposite(array, this.threadCount);
    }

    public boolean hasСomposite(int[] array, int threadCount) {
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
                    for (int j = i; j < threadCount; j++) {
                        checks[j].interrupt();
                    }
                    return true;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return false;
    }

    private static class Check extends Thread {
        private final int start;
        private final int end;
        private final int[] array;
        private boolean result = false;

        public Check(int[] array, int start, int end) {
            this.start = start;
            this.end = end;
            this.array = array;
        }

        public boolean getResult() {
            return result;
        }

        @Override
        public void run() {
            for (int i = start; i < end; i++) {
                if (Thread.currentThread().isInterrupted()) {
                    return;
                }
                if (PrimeUtils.isComposite(array[i])) {
                    result = true;
                    return;
                }
            }
        }
    }
}
