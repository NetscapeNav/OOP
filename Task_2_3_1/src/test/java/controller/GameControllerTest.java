package controller;

import model.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GameControllerTest {

    @Test
    void testHandleKeyPressChangesDirection() {
        LinkedList<Cell> body = new LinkedList<>();
        body.add(new Cell(5, 5));
        Snake snake = new Snake(body, Direction.RIGHT, true, 0);
        GameField field = new GameField(20, 15, new Random(42));
        Obstacle obstacle = new Obstacle();
        Config config = new Config(Arrays.asList(70.0, 15.0, 15.0));
        Level level = new Level(15, 20, 200, 3, 100, obstacle, 1, config);
        WinCondition wc = new WinCondition(100);
        
        GameModel model = new GameModel(snake, new ArrayList<>(), field, obstacle, level, wc, GameState.PLAYING, 0, new Random(42));

        model.setPlayerDirection(Direction.UP);
        model.tick();
        assertEquals(Direction.UP, model.getSnake().getDirection());

        model.setPlayerDirection(Direction.LEFT);
        model.tick();
        assertEquals(Direction.LEFT, model.getSnake().getDirection());
    }
}
