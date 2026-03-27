package model;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ObstacleTest {

    @Test
    void testEmptyConstructor() {
        Obstacle obs = new Obstacle();
        assertFalse(obs.contains(new Cell(0, 0)));
        assertTrue(obs.getCells().isEmpty());
    }

    @Test
    void testConstructorWithCells() {
        Set<Cell> cells = new HashSet<>();
        cells.add(new Cell(1, 1));
        cells.add(new Cell(2, 2));
        Obstacle obs = new Obstacle(cells);
        assertTrue(obs.contains(new Cell(1, 1)));
        assertTrue(obs.contains(new Cell(2, 2)));
        assertFalse(obs.contains(new Cell(3, 3)));
    }

    @Test
    void testAddCell() {
        Obstacle obs = new Obstacle();
        obs.addCell(new Cell(5, 5));
        assertTrue(obs.contains(new Cell(5, 5)));
    }

    @Test
    void testGetCells() {
        Obstacle obs = new Obstacle();
        obs.addCell(new Cell(1, 1));
        obs.addCell(new Cell(2, 2));
        assertEquals(2, obs.getCells().size());
    }
}
