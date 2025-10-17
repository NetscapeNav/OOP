package org.example;

public class Main {
    public static void main(String[] args) {
        int[] arr_orig = new int[] {5, 4, 3, 2, 1, 66};
        int[] arr = Heapsort.heapsortCopy(arr_orig);
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }
}