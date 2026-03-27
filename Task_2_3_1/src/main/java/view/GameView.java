package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import model.*;

import java.util.List;

public class GameView {
    private final Canvas canvas;
    private final int cellSize;

    public GameView(Canvas canvas, int cellSize) {
        this.canvas = canvas;
        this.cellSize = cellSize;
    }

    public void render(GameModel model) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        GameField field = model.getField();

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(0.5);
        for (int x = 0; x <= field.getWidth(); x++) {
            gc.strokeLine(x * cellSize, 0, x * cellSize, field.getHeight() * cellSize);
        }
        for (int y = 0; y <= field.getHeight(); y++) {
            gc.strokeLine(0, y * cellSize, field.getWidth() * cellSize, y * cellSize);
        }

        Obstacle obstacle = model.getObstacle();
        if (obstacle != null) {
            gc.setFill(Color.GRAY);
            for (Cell cell : obstacle.getCells()) {
                gc.fillRect(cell.getX() * cellSize, cell.getY() * cellSize, cellSize - 1, cellSize - 1);
            }
        }

        List<Food> foods = model.getFoods();
        for (Food food : foods) {
            FoodType type = food.getFoodType();
            if (type == FoodType.NORMAL) {
                gc.setFill(Color.RED);
            } else if (type == FoodType.BONUS) {
                gc.setFill(Color.GOLD);
            } else if (type == FoodType.SHRINK) {
                gc.setFill(Color.PURPLE);
            }
            Cell pos = food.getPosition();
            gc.fillOval(pos.getX() * cellSize + 2, pos.getY() * cellSize + 2, cellSize - 4, cellSize - 4);
        }

        Snake snake = model.getSnake();
        if (snake != null && snake.isAlive()) {
            gc.setFill(Color.GREEN);
            for (Cell cell : snake.getBody()) {
                gc.fillRect(cell.getX() * cellSize, cell.getY() * cellSize, cellSize - 1, cellSize - 1);
            }

            Cell head = snake.getHead();
            gc.setFill(Color.LIMEGREEN);
            gc.fillRect(head.getX() * cellSize, head.getY() * cellSize, cellSize - 1, cellSize - 1);
        }
        if (model.getState() != GameState.PLAYING) {
            gc.setFill(new Color(0, 0, 0, 0.7));
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font("Arial", 24));
            String message = model.getState() == GameState.WON ? "YOU WIN!" : "GAME OVER!";
            gc.fillText(message, canvas.getWidth() / 2 - 70, canvas.getHeight() / 2);
        }
    }
}
