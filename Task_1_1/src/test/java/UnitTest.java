import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.example.Heapsort;

public class UnitTest {
    @Test
    void testExampleArray() {
        int[] arr = {5, 4, 3, 2, 1};
        int[] expected = {1, 2, 3, 4, 5};
        Heapsort.heapsort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    void testSortedArray() {
        int[] arr = {1, 2, 3, 4, 5};
        int[] expected = {1, 2, 3, 4, 5};
        Heapsort.heapsort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    void testRevertedArray() {
        int[] arr = {10, 8, 6, 4, 2, 0};
        int[] expected = {0, 2, 4, 6, 8, 10};
        Heapsort.heapsort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    void testArrayWithDuplicates() {
        int[] arr = {5, 2, 8, 2, 5, 8, 1};
        int[] expected = {1, 2, 2, 5, 5, 8, 8};
        Heapsort.heapsort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    void testEmptyArray() {
        int[] arr = {};
        int[] expected = {};
        Heapsort.heapsort(arr);
        assertArrayEquals(expected, arr);
    }

    @Test
    void testSingleElement() {
        int[] arr = {42};
        int[] expected = {42};
        Heapsort.heapsort(arr);
        assertArrayEquals(expected, arr);
    }
}
