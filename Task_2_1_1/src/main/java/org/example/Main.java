package org.example;

import prime_first.ParallelsChecker;

public class Main {
    public static void main(String[] args) {
        int[] array = new int[] {6, 8, 7, 13, 5, 9, 4};
        ParallelsChecker checker = new ParallelsChecker();
        boolean has_composite = checker.hasComposite(array);
        System.out.printf(String.valueOf(has_composite));
    }
}