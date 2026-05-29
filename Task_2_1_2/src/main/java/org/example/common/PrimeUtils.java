package org.example.common;

import java.util.concurrent.CancellationException;
import java.util.function.BooleanSupplier;

public class PrimeUtils {
    public static boolean isComposite(int num) {
        return isComposite(num, () -> false);
    }
    public static boolean isComposite(int num, BooleanSupplier shouldCancel) {
        if (shouldCancel.getAsBoolean()) {
            throw new CancellationException();
        }

        if (num <= 2) {
            return false;
        }

        if (num % 2 == 0) {
            return true;
        }

        for (int i = 3; i <= num / i; i += 2) {
            if (shouldCancel.getAsBoolean()) {
                throw new CancellationException();
            }

            if (num % i == 0) {
                return true;
            }
        }
        return false;
    }
}
