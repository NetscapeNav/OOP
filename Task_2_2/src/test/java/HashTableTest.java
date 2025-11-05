import hashtable.HashTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HashTableTest {
    private HashTable<String, Integer> table;

    @BeforeEach
    void setUp() {
        table = new HashTable<>();
    }

    @Test
    void testPutAndGet() {
        table.put("key1", 100);
        assertEquals(100, table.get("key1"));
    }

    @Test
    void testPutOverwrite() {
        table.put("key1", 100);
        table.put("key1", 200);
        assertEquals(200, table.get("key1"));
    }

    @Test
    void testExamplePutUpdateGet() {
        HashTable<String, Number> hashTable = new HashTable<>();
        hashTable.put("one", 1);
        hashTable.update("one", 1.0);
        assertEquals(1.0, hashTable.get("one"));
    }
}
