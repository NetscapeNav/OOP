import hashtable.HashTable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HashTableTest {
    @Test
    void test() {
        HashTable<String, Number> hashTable = new HashTable<>();
        hashTable.put("one", 1);
        hashTable.update("one", 1.0);
        assertEquals(1.0, hashTable.get("one"));
    }
}
