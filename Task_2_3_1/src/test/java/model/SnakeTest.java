package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class SnakeTest {

    private Snake snake;

    @BeforeEach
    void setUp() {
        Deque<Cell> body = new LinkedList<>();
        body.add(new Cell(5, 5));
        snake = new Snake(body, Direction.RIGHT, true, 0);
    }

    @Test
    void testInitialState() {
        assertEquals(1, snake.length());
        assertEquals(new Cell(5, 5), snake.getHead());
        assertEquals(new Cell(5, 5), snake.getTail());
        assertEquals(Direction.RIGHT, snake.getDirection());
        assertTrue(snake.isAlive());
    }

    @Test
    void testMoveRight() {
        Cell newHead = snake.move();
        assertEquals(new Cell(6, 5), newHead);
        assertEquals(new Cell(6, 5), snake.getHead());
        assertEquals(1, snake.length());
    }

    @Test
    void testMoveUp() {
        snake.setDirection(Direction.UP);
        Cell newHead = snake.move();
        assertEquals(new Cell(5, 4), newHead);
    }

    @Test
    void testMovePreservesLength() {
        snake.move();
        snake.move();
        snake.move();
        assertEquals(1, snake.length());
    }

    @Test
    void testGrowIncreasesLength() {
        snake.grow();
        snake.move();
        assertEquals(2, snake.length());
    }

    @Test
    void testGrowMultiple() {
        snake.grow();
        snake.grow();
        snake.move();
        snake.move();
        assertEquals(3, snake.length());
    }

    @Test
    void testGrowAndMoveSequence() {
        snake.grow();
        snake.move(); 
        snake.move(); 
        assertEquals(2, snake.length());
    }

    @Test
    void testSetDirectionPreventsReversal() {
        snake.grow();
        snake.move(); 
        snake.setDirection(Direction.LEFT); 
        snake.move();
        assertEquals(Direction.RIGHT, snake.getDirection());
    }

    @Test
    void testSetDirectionAllowsPerpendicularTurn() {
        snake.setDirection(Direction.UP);
        snake.move();
        assertEquals(Direction.UP, snake.getDirection());
    }

    @Test
    void testSetDirectionAllowsSameDirection() {
        snake.setDirection(Direction.RIGHT);
        snake.move();
        assertEquals(Direction.RIGHT, snake.getDirection());
    }

    @Test
    void testKill() {
        snake.kill();
        assertFalse(snake.isAlive());
    }

    @Test
    void testHasSelfCollisionFalseForShortSnake() {
        assertFalse(snake.hasSelfCollision());
    }

    @Test
    void testHasSelfCollisionFalseForLongSnake() {
        snake.grow();
        snake.grow();
        snake.grow();
        snake.move();
        snake.move();
        snake.move();
        assertFalse(snake.hasSelfCollision());
    }

    @Test
    void testGetOccupiedCells() {
        snake.grow();
        snake.move();
        Set<Cell> cells = snake.getOccupiedCells();
        assertEquals(2, cells.size());
        assertTrue(cells.contains(snake.getHead()));
        assertTrue(cells.contains(snake.getTail()));
    }

    @Test
    void testGetBody() {
        assertNotNull(snake.getBody());
        assertEquals(1, snake.getBody().size());
    }

    @Test
    void testShrink() {
        snake.grow();
        snake.grow();
        snake.move();
        snake.move();
        assertEquals(3, snake.length());
        snake.shrink();
        assertEquals(2, snake.length());
    }

    @Test
    void testShrinkMinLength() {
        assertEquals(1, snake.length());
        snake.shrink();
        assertEquals(1, snake.length());
    }

    @Test
    void testMoveReturnsNewHead() {
        Cell head = snake.move();
        assertEquals(snake.getHead(), head);
    }
}
