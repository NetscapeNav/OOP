package org.example;

/**
 * Этот класс реализует алгоритм пирамидальной сортировки
 */
public class Heapsort {
    /**
     * Метод, который сортирует массив пирамидальной сортировкой
     *
     * @param arr Массив целых чисел для сортировки
     */
    public static void heapsort(int[] arr) {
        if (arr == null || arr.length == 0) {
            return;
        }
        int n = arr.length;
        for (int i = n/2 - 1; i >= 0; i--) {
            heapify(arr, n, i);
        }
        for (int i = n-1; i > 0; i--) {
            int temp = arr[i];
            arr[i] = arr[0];
            arr[0] = temp;
            heapify(arr, i, 0);
        }
    }

    /**
     * Метод, который выполняет сортировку кучей
     *
     * @param arr Массив целых чисел для сортировки,
     * @param heapSize Размер массива,
     * @param rootIndex Индекс рассматриваемого узла кучи
     */
    private static void heapify(int[] arr, int heapSize, int rootIndex) {
        int left = rootIndex * 2 + 1;
        int right = rootIndex * 2 + 2;
        int largest = rootIndex;
        if (left < heapSize && arr[left] > arr[largest]) {
            largest = left;
        }
        if (right < heapSize && arr[right] > arr[largest]) {
            largest = right;
        }
        if (largest != rootIndex) {
            int temp = arr[rootIndex];
            arr[rootIndex] = arr[largest];
            arr[largest] = temp;
            heapify(arr, heapSize, largest);
        }
    }

}
