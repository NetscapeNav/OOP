package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class GameModelTest {

    private GameModel createModel(int width, int height, int winLength, Random random) {
        Deque<Cell> snakeBody = new LinkedList<>();
        snakeBody.add(new Cell(width / 2, height / 2));
        Snake snake = new Snake(snakeBody, Direction.RIGHT, true, 0);
        GameField field = new GameField(width, height, random);
        List<Food> foods = new ArrayList<>();
        Obstacle obstacle = new Obstacle();
        Config config = new Config(Arrays.asList(70.0, 15.0, 15.0));
        Level level = new Level(height, width, 200, 3, winLength, obstacle, 1, config);
        WinCondition wc = new WinCondition(winLength);
        return new GameModel(snake, foods, field, obstacle, level, wc, GameState.PLAYING, 0, random);
    }

    private GameModel model;

    @BeforeEach
    void setUp() {
        model = createModel(20, 15, 100, new Random(42));
    }

    @Test
    void testInitialState() {
        assertEquals(GameState.PLAYING, model.getState());
        assertEquals(0, model.getScore());
        assertNotNull(model.getSnake());
        assertNotNull(model.getField());
        assertNotNull(model.getObstacle());
        assertNotNull(model.getLevel());
    }

    @Test
    void testGetters() {
        assertEquals(20, model.getField().getWidth());
        assertEquals(15, model.getField().getHeight());
        assertEquals(1, model.getSnake().length());
    }

    @Test
    void testTickMovesSnake() {
        Cell initialHead = model.getSnake().getHead();
        model.tick();
        Cell newHead = model.getSnake().getHead();
        assertNotEquals(initialHead, newHead);
        assertEquals(initialHead.getX() + 1, newHead.getX());
        assertEquals(initialHead.getY(), newHead.getY());
    }

    @Test
    void testTickDoesNothingWhenNotPlaying() {
        GameModel m = createModel(20, 15, 100, new Random(42));
        m.setPlayerDirection(Direction.LEFT);
        for (int i = 0; i < 20; i++) {
            m.tick();
            if (m.getState() != GameState.PLAYING) break;
        }
        assertEquals(GameState.LOST, m.getState());
        Cell headAfterLoss = m.getSnake().getHead();
        m.tick();
        assertEquals(headAfterLoss, m.getSnake().getHead());
    }

    @Test
    void testWallCollisionLost() {
        model.setPlayerDirection(Direction.RIGHT);
        for (int i = 0; i < 20; i++) {
            model.tick();
            if (model.getState() == GameState.LOST) break;
        }
        assertEquals(GameState.LOST, model.getState());
    }

    @Test
    void testObstacleCollisionLost() {
        Deque<Cell> snakeBody = new LinkedList<>();
        snakeBody.add(new Cell(3, 5));
        Snake snake = new Snake(snakeBody, Direction.RIGHT, true, 0);
        Random random = new Random(42);
        GameField field = new GameField(20, 15, random);
        List<Food> foods = new ArrayList<>();
        Obstacle obstacle = new Obstacle();
        obstacle.addCell(new Cell(4, 5));
        Config config = new Config(Arrays.asList(70.0, 15.0, 15.0));
        Level level = new Level(15, 20, 200, 3, 100, obstacle, 1, config);
        WinCondition wc = new WinCondition(100);
        GameModel m = new GameModel(snake, foods, field, obstacle, level, wc, GameState.PLAYING, 0, random);

        m.tick();
        assertEquals(GameState.LOST, m.getState());
    }

    @Test
    void testSetPlayerDirection() {
        model.setPlayerDirection(Direction.UP);
        assertEquals(Direction.UP, model.getSnake().getDirection());
    }

    @Test
    void testSpawnFoodAddsFood() {
        assertTrue(model.getFoods().isEmpty());
        model.spawnFood();
        assertEquals(1, model.getFoods().size());
    }

    @Test
    void testSpawnFoodMultiple() {
        model.spawnFood();
        model.spawnFood();
        model.spawnFood();
        assertEquals(3, model.getFoods().size());
    }

    @Test
    void testSpawnFoodPosition() {
        model.spawnFood();
        Food food = model.getFoods().get(0);
        assertTrue(model.getField().isInBounds(food.getPosition()));
    }

    @Test
    void testFoodConsumption() {
        Cell nextHead = new Cell(model.getSnake().getHead().getX() + 1, model.getSnake().getHead().getY());
        model.getFoods().add(new Food(nextHead, FoodType.NORMAL));
        int initialScore = model.getScore();

        model.tick();

        assertTrue(model.getScore() > initialScore);
    }

    @Test
    void testFoodConsumptionNormalGrowth() {
        Cell nextHead = new Cell(model.getSnake().getHead().getX() + 1, model.getSnake().getHead().getY());
        model.getFoods().add(new Food(nextHead, FoodType.NORMAL));

        model.tick();

        assertEquals(1, model.getScore());
    }

    @Test
    void testFoodConsumptionBonusGrowth() {
        Cell nextHead = new Cell(model.getSnake().getHead().getX() + 1, model.getSnake().getHead().getY());
        model.getFoods().add(new Food(nextHead, FoodType.BONUS));

        model.tick();

        assertEquals(3, model.getScore());
    }

    @Test
    void testFoodConsumptionShrink() {
        Deque<Cell> snakeBody = new LinkedList<>();
        snakeBody.add(new Cell(5, 5));
        snakeBody.add(new Cell(4, 5));
        snakeBody.add(new Cell(3, 5));
        Snake snake = new Snake(snakeBody, Direction.RIGHT, true, 0);
        Random random = new Random(42);
        GameField field = new GameField(20, 15, random);
        List<Food> foods = new ArrayList<>();
        foods.add(new Food(new Cell(6, 5), FoodType.SHRINK));
        Obstacle obstacle = new Obstacle();
        Config config = new Config(Arrays.asList(70.0, 15.0, 15.0));
        Level level = new Level(15, 20, 200, 3, 100, obstacle, 1, config);
        WinCondition wc = new WinCondition(100);
        GameModel m = new GameModel(snake, foods, field, obstacle, level, wc, GameState.PLAYING, 0, random);

        m.tick();

        assertEquals(1, m.getScore());
    }

    @Test
    void testFoodReplacedAfterConsumption() {
        Cell nextHead = new Cell(model.getSnake().getHead().getX() + 1, model.getSnake().getHead().getY());
        model.getFoods().add(new Food(nextHead, FoodType.NORMAL));

        model.tick();

        assertEquals(1, model.getFoods().size());
    }

    @Test
    void testWinConditionMet() {
        Deque<Cell> snakeBody = new LinkedList<>();
        for (int i = 0; i < 4; i++) {
            snakeBody.add(new Cell(5 - i, 5));
        }
        Snake snake = new Snake(snakeBody, Direction.RIGHT, true, 0);
        Random random = new Random(42);
        GameField field = new GameField(20, 15, random);
        List<Food> foods = new ArrayList<>();
        foods.add(new Food(new Cell(6, 5), FoodType.NORMAL));
        Obstacle obstacle = new Obstacle();
        Config config = new Config(Arrays.asList(70.0, 15.0, 15.0));
        Level level = new Level(15, 20, 200, 3, 5, obstacle, 1, config);
        WinCondition wc = new WinCondition(5);
        GameModel m = new GameModel(snake, foods, field, obstacle, level, wc, GameState.PLAYING, 0, random);

        m.tick();

        m.tick();
        assertEquals(GameState.WON, m.getState());
    }

    @Test
    void testGameStateEnum() {
        assertNotNull(GameState.PLAYING);
        assertNotNull(GameState.WON);
        assertNotNull(GameState.LOST);
        assertEquals(3, GameState.values().length);
    }

    @Test
    void testMultipleTicks() {
        for (int i = 0; i < 5; i++) {
            model.tick();
        }
        assertEquals(GameState.PLAYING, model.getState());
    }

    @Test
    void testNullObstacleHandling() {
        Deque<Cell> snakeBody = new LinkedList<>();
        snakeBody.add(new Cell(5, 5));
        Snake snake = new Snake(snakeBody, Direction.RIGHT, true, 0);
        Random random = new Random(42);
        GameField field = new GameField(20, 15, random);
        List<Food> foods = new ArrayList<>();
        Config config = new Config(Arrays.asList(70.0, 15.0, 15.0));
        Level level = new Level(15, 20, 200, 3, 100, null, 1, config);
        WinCondition wc = new WinCondition(100);
        GameModel m = new GameModel(snake, foods, field, null, level, wc, GameState.PLAYING, 0, random);
        m.spawnFood();
        assertEquals(1, m.getFoods().size());
    }
}
