package controller;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import model.*;
import view.GameView;

import java.util.*;

public class GameController {

    @FXML private Canvas gameCanvas;
    @FXML private Label scoreLabel;
    @FXML private Label lengthLabel;
    @FXML private Label levelLabel;
    @FXML private Label statusLabel;

    private GameModel model;
    private GameView view;
    private AnimationTimer gameLoop;
    private long lastUpdate;
    private boolean isPaused;
    private Level currentLevel;

    public void initGame(Level level) {
        this.currentLevel = level;
        this.isPaused = false;
        startNewGame();

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isPaused && model != null && model.getState() == GameState.PLAYING) {
                    if (now - lastUpdate >= currentLevel.getTickInterval() * 1_000_000L) {
                        model.tick();
                        updateUI();
                        lastUpdate = now;
                    }
                }
                if (view != null && model != null) {
                    view.render(model);
                }
            }
        };
        gameLoop.start();
    }

    private void startNewGame() {
        Deque<Cell> snakeBody = new LinkedList<>();
        int startX = currentLevel.getWidth() / 2;
        int startY = currentLevel.getHeight() / 2;
        snakeBody.add(new Cell(startX, startY));

        Snake snake = new Snake(snakeBody, Direction.RIGHT, true, 0);

        Random random = new Random();
        GameField field = new GameField(currentLevel.getWidth(), currentLevel.getHeight(), random);

        List<Food> foods = new ArrayList<>();

        model = new GameModel(snake, foods, field, currentLevel.getObstacles(),
                currentLevel, new WinCondition(currentLevel.getWinLength()),
                GameState.PLAYING, 0, random);

        for (int i = 0; i < currentLevel.getFoodCount(); i++) {
            model.spawnFood();
        }
        int cellSize = calculateCellSize();
        view = new GameView(gameCanvas, cellSize);

        updateUI();
        lastUpdate = System.nanoTime();
    }

    private int calculateCellSize() {
        int canvasWidth = (int) gameCanvas.getWidth();
        int canvasHeight = (int) gameCanvas.getHeight();
        int cellWidth = canvasWidth / currentLevel.getWidth();
        int cellHeight = canvasHeight / currentLevel.getHeight();
        return Math.min(cellWidth, cellHeight);
    }

    private void updateUI() {
        if (model != null) {
            scoreLabel.setText(String.valueOf(model.getScore()));
            lengthLabel.setText(String.valueOf(model.getSnake().length()));
            levelLabel.setText(currentLevel.getLevelNumber() > 0 ? String.valueOf(currentLevel.getLevelNumber()) : "Custom");

            switch (model.getState()) {
                case PLAYING:
                    statusLabel.setText("Playing");
                    statusLabel.setStyle("-fx-text-fill: green; -fx-font-size: 18px; -fx-font-weight: bold;");
                    break;
                case WON:
                    statusLabel.setText("YOU WIN!");
                    statusLabel.setStyle("-fx-text-fill: gold; -fx-font-size: 18px; -fx-font-weight: bold;");
                    gameLoop.stop();
                    break;
                case LOST:
                    statusLabel.setText("GAME OVER");
                    statusLabel.setStyle("-fx-text-fill: red; -fx-font-size: 18px; -fx-font-weight: bold;");
                    gameLoop.stop();
                    break;
            }
        }
    }

    public void handleKeyPress(KeyEvent event) {
        if (model == null || model.getState() != GameState.PLAYING) {
            return;
        }

        KeyCode code = event.getCode();
        switch (code) {
            case UP:
            case W:
                model.setPlayerDirection(Direction.UP);
                break;
            case DOWN:
            case S:
                model.setPlayerDirection(Direction.DOWN);
                break;
            case LEFT:
            case A:
                model.setPlayerDirection(Direction.LEFT);
                break;
            case RIGHT:
            case D:
                model.setPlayerDirection(Direction.RIGHT);
                break;
            case SPACE:
                handlePause();
                break;
            default:
                break;
        }
    }

    @FXML
    private void handleNewGame() {
        gameLoop.stop();
        startNewGame();
        isPaused = false;
        lastUpdate = System.nanoTime();
        gameLoop.start();
    }

    @FXML
    private void handlePause() {
        if (model.getState() != GameState.PLAYING) {
            return;
        }

        isPaused = !isPaused;
        if (isPaused) {
            statusLabel.setText("PAUSED");
            statusLabel.setStyle("-fx-text-fill: orange; -fx-font-size: 18px; -fx-font-weight: bold;");
        } else {
            statusLabel.setText("Playing");
            statusLabel.setStyle("-fx-text-fill: green; -fx-font-size: 18px; -fx-font-weight: bold;");
            lastUpdate = System.nanoTime();
        }
    }

    @FXML
    private void handleMainMenu() {
        try {
            gameLoop.stop();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MenuView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) gameCanvas.getScene().getWindow();
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("Snake Game - Main Menu");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}