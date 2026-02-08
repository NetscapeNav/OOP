package elements;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TableTest {
    @Test
    void testTable() {
        Table table = new Table.Builder()
                .withAlignments(Table.ALIGN_LEFT, Table.ALIGN_CENTER, Table.ALIGN_RIGHT)
                .addRow("Left", "Center", "Right")
                .addRow("L", "C", "R")
                .build();

        String result = table.serialize();

        assertTrue(result.contains(" :----: "), "Should contain center alignment");
        assertTrue(result.contains(" ----: "), "Should contain right alignment");
        assertTrue(result.contains("Left"));
        assertTrue(result.contains("R"));
    }

    @Test
    void testTableWithElements() {
        Table table = new Table.Builder()
                .addRow(new Text.Bold("Bold"), new Text("Normal"))
                .build();

        String result = table.serialize();
        assertTrue(result.contains("**Bold**"));
    }

    @Test
    void testEmptyTable() {
        Table table = new Table.Builder().build();
        assertEquals("", table.serialize());
    }

    @Test
    void testEqualsAndHashCode() {
        Table t1 = new Table.Builder()
                .withAlignments(Table.ALIGN_CENTER)
                .withRowLimit(5)
                .addRow("A", "B")
                .build();

        Table t2 = new Table.Builder()
                .withAlignments(Table.ALIGN_CENTER)
                .withRowLimit(5)
                .addRow("A", "B")
                .build();

        Table t3 = new Table.Builder()
                .addRow("Different")
                .build();

        assertEquals(t1, t1);
        assertEquals(t1, t2);
        assertEquals(t1.hashCode(), t2.hashCode());
        assertNotEquals(t1, t3);
        assertNotEquals(t1, null);
        assertNotEquals(t1, "String");
    }
}
