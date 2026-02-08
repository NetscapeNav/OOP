import hashtable.HashTable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ConcurrentModificationException;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class HashTableTest {
    private HashTable<String, Integer> table;

    @BeforeEach
    void setUp() {
        table = new HashTable<>();
    }

    @Test
    void testExample() {
        HashTable<String, Number> hashTable = new HashTable<>();
        hashTable.put("one", 1);
        hashTable.update("one", 1.0);
        assertEquals(1.0, hashTable.get("one"));
    }

    @Test
    void testPutOverwrite() {
        table.put("key1", 100);
        table.put("key1", 200);
        assertEquals(200, table.get("key1"));
    }

    @Test
    void testGetNonexistent() {
        assertNull(table.get("doesntexist"));
    }

    @Test
    void testRemove() {
        table.put("key1", 100);
        table.remove("key1");
        assertNull(table.get("key1"));
    }

    @Test
    void testUpdate() {
        table.put("key1", 100);
        table.update("key1", 200);
        assertEquals(200, table.get("key1"));
    }

    @Test
    void testContainsKey() {
        table.put("test", 1);
        assertTrue(table.containsKey("test"));
        assertFalse(table.containsKey("missing"));
    }

    @Test
    void testIterator() {
        table.put("a", 1);
        table.put("b", 2);
        table.put("c", 3);

        int count = 0;
        for (HashTable.Entry<String, Integer> entry : table) {
            assertNotNull(entry);
            assertNotNull(entry.getKey());
            assertNotNull(entry.getValue());
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    void testEqualsSameTable() {
        table.put("a", 1);
        table.put("b", 2);

        assertEquals(table, table);
    }

    @Test
    void testEqualsDifferentTables() {
        HashTable<String, Integer> table1 = new HashTable<>();
        table1.put("a", 1);
        table1.put("b", 2);

        HashTable<String, Integer> table2 = new HashTable<>();
        table2.put("a", 1);
        table2.put("b", 2);

        assertEquals(table1, table2);
        assertEquals(table2, table1);
    }

    @Test
    void testEqualsDifferentSize() {
        HashTable<String, Integer> table1 = new HashTable<>();
        table1.put("a", 1);

        HashTable<String, Integer> table2 = new HashTable<>();
        table2.put("a", 1);
        table2.put("b", 2);

        assertNotEquals(table1, table2);
    }

    @Test
    void testEqualsDifferentValues() {
        HashTable<String, Integer> table1 = new HashTable<>();
        table1.put("a", 1);

        HashTable<String, Integer> table2 = new HashTable<>();
        table2.put("a", 2);

        assertNotEquals(table1, table2);
    }

    @Test
    void testEqualsNull() {
        assertNotEquals(table, null);
    }

    @Test
    void testEqualsDifferentType() {
        assertNotEquals(table, "not a hashtable");
    }

    @Test
    void testToStringEmpty() {
        assertEquals("{}", table.toString());
    }

    @Test
    void testToStringWithElements() {
        table.put("a", 1);
        table.put("b", 2);

        String result = table.toString();
        assertTrue(result.contains("a=1"));
        assertTrue(result.contains("b=2"));
        assertTrue(result.startsWith("{"));
        assertTrue(result.endsWith("}"));
    }
}
