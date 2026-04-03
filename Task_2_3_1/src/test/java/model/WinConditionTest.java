package model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class WinConditionTest {

    @Test
    void testWinNotSatisfiedShortSnake() {
        WinCondition wc = new WinCondition(5);
        java.util.Deque<Cell> body = new java.util.LinkedList<>();
        body.add(new Cell(0, 0));
        Snake snake = new Snake(body, Direction.RIGHT, true, 0);
        assertFalse(wc.isW(snake));
    }

    @Test
    void testWinSatisfiedExactLength() {
        WinCondition wc = new WinCondition(3);
        java.util.Deque<Cell> body = new java.util.LinkedList<>();
        body.add(new Cell(2, 0));
        body.add(new Cell(1, 0));
        body.add(new Cell(0, 0));
        Snake snake = new Snake(body, Direction.RIGHT, true, 0);
        assertTrue(wc.isW(snake));
    }

    @Test
    void testWinSatisfiedLongerThanTarget() {
        WinCondition wc = new WinCondition(2);
        java.util.Deque<Cell> body = new java.util.LinkedList<>();
        body.add(new Cell(2, 0));
        body.add(new Cell(1, 0));
        body.add(new Cell(0, 0));
        Snake snake = new Snake(body, Direction.RIGHT, true, 0);
        assertTrue(wc.isW(snake));
    }

    @Test
    void testWinConditionInterface() {
        WinConditionInterface wci = new WinCondition(1);
        java.util.Deque<Cell> body = new java.util.LinkedList<>();
        body.add(new Cell(0, 0));
        Snake snake = new Snake(body, Direction.RIGHT, true, 0);
        assertTrue(wci.isW(snake));
    }
}
