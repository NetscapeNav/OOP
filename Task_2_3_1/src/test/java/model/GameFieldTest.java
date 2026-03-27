package model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class GameFieldTest {

    @Test
    void testIsInBoundsCenter() {
        GameField field = new GameField(20, 15, new Random());
        assertTrue(field.isInBounds(new Cell(10, 7)));
    }

    @Test
    void testIsInBoundsTopLeft() {
        GameField field = new GameField(20, 15, new Random());
        assertTrue(field.isInBounds(new Cell(0, 0)));
    }

    @Test
    void testIsInBoundsBottomRight() {
        GameField field = new GameField(20, 15, new Random());
        assertTrue(field.isInBounds(new Cell(19, 14)));
    }

    @Test
    void testIsOutOfBoundsNegativeX() {
        GameField field = new GameField(20, 15, new Random());
        assertFalse(field.isInBounds(new Cell(-1, 5)));
    }

    @Test
    void testIsOutOfBoundsNegativeY() {
        GameField field = new GameField(20, 15, new Random());
        assertFalse(field.isInBounds(new Cell(5, -1)));
    }

    @Test
    void testIsOutOfBoundsOverWidth() {
        GameField field = new GameField(20, 15, new Random());
        assertFalse(field.isInBounds(new Cell(20, 5)));
    }

    @Test
    void testIsOutOfBoundsOverHeight() {
        GameField field = new GameField(20, 15, new Random());
        assertFalse(field.isInBounds(new Cell(5, 15)));
    }

    @Test
    void testGetRandomFreeCellReturnsValidCell() {
        Random random = new Random(42);
        GameField field = new GameField(10, 10, random);
        Set<Cell> reserved = new HashSet<>();
        Cell free = field.getRandomFreeCell(reserved);
        assertNotNull(free);
        assertTrue(field.isInBounds(free));
    }

    @Test
    void testGetRandomFreeCellAvoidsReserved() {
        Random random = new Random(42);
        GameField field = new GameField(3, 3, random);
        Set<Cell> reserved = new HashSet<>();
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (x != 1 || y != 1) {
                    reserved.add(new Cell(x, y));
                }
            }
        }
        
        Cell free = field.getRandomFreeCell(reserved);
        assertEquals(new Cell(1, 1), free);
    }

    @Test
    void testGetRandomFreeCellReturnsNullWhenFull() {
        Random random = new Random(42);
        GameField field = new GameField(2, 2, random);
        Set<Cell> reserved = new HashSet<>();
        reserved.add(new Cell(0, 0));
        reserved.add(new Cell(0, 1));
        reserved.add(new Cell(1, 0));
        reserved.add(new Cell(1, 1));
        assertNull(field.getRandomFreeCell(reserved));
    }

    @Test
    void testGetWidthAndHeight() {
        GameField field = new GameField(20, 15, new Random());
        assertEquals(20, field.getWidth());
        assertEquals(15, field.getHeight());
    }
}
