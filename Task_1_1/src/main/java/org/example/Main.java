package org.example;

/**
 * Этот класс вызывает класс Heapsort для сортировки имеющегося массива.
 */
public class Main {
    /**
     * Метод является точкой входа в программу.
     * Сортирует предопределённый массив пирамидальной сортировкой и выводит результат.
     *
     * @param args Аргументы командной строки.
     */
    public static void main(String[] args) {
        int[] arr = new int[] {5, 4, 3, 2, 1, 66};
        Heapsort.heapsort(arr);
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }
}