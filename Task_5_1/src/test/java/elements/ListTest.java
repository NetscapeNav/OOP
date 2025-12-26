package elements;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ListTest {
    @Test
    void testOrderedList() {
        MDList list = new MDList(true, "Item 1", "Item 2");
        String res = list.serialize();
        assertTrue(res.contains("1. Item 1"));
        assertTrue(res.contains("2. Item 2"));
    }

    @Test
    void testUnorderedList() {
        MDList list = new MDList(false, "Item A", "Item B");
        String res = list.serialize();
        assertTrue(res.contains("- Item A"));
        assertTrue(res.contains("- Item B"));
    }
    
    @Test
    void testNestedElements() {
        MDList list = new MDList(false, new Text.Bold("Bold Item"));
        String res = list.serialize();
        assertTrue(res.contains("- **Bold Item**"));
    }
}
