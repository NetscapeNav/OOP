package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DirectionTest {

    @Test
    void testDirectionOffsets() {
        assertEquals(0, Direction.UP.getX());
        assertEquals(-1, Direction.UP.getY());

        assertEquals(0, Direction.DOWN.getX());
        assertEquals(1, Direction.DOWN.getY());

        assertEquals(-1, Direction.LEFT.getX());
        assertEquals(0, Direction.LEFT.getY());

        assertEquals(1, Direction.RIGHT.getX());
        assertEquals(0, Direction.RIGHT.getY());
    }

    @Test
    void testOpposite() {
        assertEquals(Direction.DOWN, Direction.UP.opposite());
        assertEquals(Direction.UP, Direction.DOWN.opposite());
        assertEquals(Direction.RIGHT, Direction.LEFT.opposite());
        assertEquals(Direction.LEFT, Direction.RIGHT.opposite());
    }
}
