package org.example;

import prime_first.DistributedChecker;

public class Main {
    public static void main(String[] args) {
        int[] array = new int[] {6, 8, 7, 13, 5, 9, 4};
        DistributedChecker checker = new DistributedChecker();
        boolean has_composite = checker.hasComposite(array);
        System.out.println("Result for array 1: " + has_composite);
        
        int[] array2 = new int[] {20319251, 6997901, 6997927, 6997937, 17858849, 6997967, 
                                 6998009, 6998029, 6998039, 20165149, 6998051, 6998053};
        boolean has_composite2 = checker.hasComposite(array2);
        System.out.println("Result for array 2: " + has_composite2);
    }
}