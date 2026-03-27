package model;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class GameModel {
    private final Snake snake;
    private final List<Food> foods;
    private final GameField field;
    private final Obstacle obstacle;
    private final Level level;
    private final WinConditionInterface winCond;
    private GameState state;
    private int score;
    private final Random random;

    public GameModel(Snake snake, List<Food> foods, GameField field, Obstacle obstacle, Level level, WinConditionInterface winCond, GameState state, int score, Random random) {
        this.snake = snake;
        this.foods = foods;
        this.field = field;
        this.obstacle = obstacle;
        this.level = level;
        this.winCond = winCond;
        this.state = state;
        this.score = score;
        this.random = random;
    }

    public void setPlayerDirection(Direction direction) {
        snake.setDirection(direction);
    }

    public void tick() {
        if (state != GameState.PLAYING) {
            return;
        }

        Cell newHead = snake.move();

        if (!field.isInBounds(newHead) || obstacle.contains(newHead) || snake.hasSelfCollision()) {
            snake.kill();
            state = GameState.LOST;
        }

        if (state == GameState.LOST) {
            return;
        }

        for (int i = 0; i < foods.size(); i++) {
            Food f = foods.get(i);
            if (f.getPosition().equals(newHead)) {
                int growth = f.getFoodType().getGrowthValue();
                for (int grown = 0; grown < growth; grown++) {
                    snake.grow();
                }
                if (growth < 0) {
                    snake.shrink();
                }
                score += Math.max(1, growth);
                foods.remove(i);
                spawnFood();
                break;
            }
        }

        if (winCond.isW(snake)) {
            state = GameState.WON;
        }
    }

    public void spawnFood() {
        Set<Cell> reserved = getAllOccupiedCells();
        Cell position = field.getRandomFreeCell(reserved);
        if (position != null) {
            int randomIndex = random.nextInt(100);
            FoodType selectedType;

            if (randomIndex < 70) {
                selectedType = FoodType.NORMAL;
            } else if (randomIndex < 85) {
                selectedType = FoodType.BONUS;
            } else {
                selectedType = FoodType.SHRINK;
            }

            foods.add(new Food(position, selectedType));
        }
    }

    private Set<Cell> getAllOccupiedCells() {
        Set<Cell> occupied = snake.getOccupiedCells();
        if (obstacle != null) {
            occupied.addAll(obstacle.getCells());
        }
        for (Food food : foods) {
            occupied.add(food.getPosition());
        }
        return occupied;
    }

    public Level getLevel() {
        return level;
    }

    public GameState getState() {
        return state;
    }

    public List<Food> getFoods() {
        return foods;
    }

    public Snake getSnake() {
        return snake;
    }

    public Obstacle getObstacle() {
        return obstacle;
    }

    public GameField getField() {
        return field;
    }

    public int getScore() {
        return score;
    }
}
