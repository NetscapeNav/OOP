package org.example;

import org.example.master.DistributedChecker;

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

        DistributedChecker lowChecker = new DistributedChecker(
                new String[]{"127.0.0.1", "127.0.0.1", "127.0.0.1"},
                new int[]{8080, 8081, 8082},
                1
        );
        int[] array3 = new int[] {2147483647, 2147483629, 2147483587, 2147483579, 2147483563,
                2147483549, 2147483543, 2147483497, 2147483489, 2147483477, 2147483423, 2147483399};
        boolean has_composite_on_low = lowChecker.hasComposite(array3);
        System.out.println("Result for array 3 with chunkSize = 1: " + has_composite_on_low);
    }
}