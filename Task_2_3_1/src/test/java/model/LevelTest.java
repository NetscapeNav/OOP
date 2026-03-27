package model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LevelTest {

    @Test
    void testConstructorAndGetters() {
        Obstacle obs = new Obstacle();
        Level level = new Level(15, 20, 200, 3, 10, obs, 1);
        assertEquals(15, level.getHeight());
        assertEquals(20, level.getWidth());
        assertEquals(200, level.getTickInterval());
        assertEquals(3, level.getFoodCount());
        assertEquals(10, level.getWinLength());
        assertEquals(obs, level.getObstacles());
        assertEquals(1, level.getLevelNumber());
    }

    @Test
    void testDefaultLevelsNotNull() {
        List<Level> levels = Level.defaultLevels();
        assertNotNull(levels);
        assertFalse(levels.isEmpty());
    }

    @Test
    void testDefaultLevelsSpeedIncreases() {
        List<Level> levels = Level.defaultLevels();
        assertTrue(levels.size() >= 2);
        assertTrue(levels.get(0).getTickInterval() > levels.get(1).getTickInterval());
    }

    @Test
    void testDefaultLevelsWinLengthIncreases() {
        List<Level> levels = Level.defaultLevels();
        assertTrue(levels.get(0).getWinLength() < levels.get(1).getWinLength());
    }

    @Test
    void testDefaultLevelsHaveCorrectNumbers() {
        List<Level> levels = Level.defaultLevels();
        for (int i = 0; i < levels.size(); i++) {
            assertEquals(i + 1, levels.get(i).getLevelNumber());
        }
    }
}
