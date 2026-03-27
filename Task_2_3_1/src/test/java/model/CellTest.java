package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CellTest {

    @Test
    void testGetters() {
        Cell cell = new Cell(5, 10);
        assertEquals(5, cell.getX());
        assertEquals(10, cell.getY());
    }

    @Test
    void testMoveUp() {
        Cell cell = new Cell(5, 5);
        Cell moved = cell.move(Direction.UP);
        assertEquals(5, moved.getX());
        assertEquals(4, moved.getY());
    }

    @Test
    void testMoveDown() {
        Cell cell = new Cell(5, 5);
        Cell moved = cell.move(Direction.DOWN);
        assertEquals(5, moved.getX());
        assertEquals(6, moved.getY());
    }

    @Test
    void testMoveLeft() {
        Cell cell = new Cell(5, 5);
        Cell moved = cell.move(Direction.LEFT);
        assertEquals(4, moved.getX());
        assertEquals(5, moved.getY());
    }

    @Test
    void testMoveRight() {
        Cell cell = new Cell(5, 5);
        Cell moved = cell.move(Direction.RIGHT);
        assertEquals(6, moved.getX());
        assertEquals(5, moved.getY());
    }

    @Test
    void testMoveDoesNotMutateOriginal() {
        Cell cell = new Cell(3, 3);
        cell.move(Direction.RIGHT);
        assertEquals(3, cell.getX());
        assertEquals(3, cell.getY());
    }

    @Test
    void testEquals() {
        Cell a = new Cell(1, 2);
        Cell b = new Cell(1, 2);
        Cell c = new Cell(2, 1);
        assertEquals(a, b);
        assertNotEquals(a, c);
    }

    @Test
    void testEqualsSameObject() {
        Cell a = new Cell(1, 2);
        assertEquals(a, a);
    }

    @Test
    void testEqualsNull() {
        Cell a = new Cell(1, 2);
        assertNotEquals(null, a);
    }

    @Test
    void testEqualsDifferentType() {
        Cell a = new Cell(1, 2);
        assertNotEquals("not a cell", a);
    }

    @Test
    void testHashCode() {
        Cell a = new Cell(1, 2);
        Cell b = new Cell(1, 2);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testToString() {
        Cell cell = new Cell(3, 7);
        assertEquals("(3, 7)", cell.toString());
    }
}
