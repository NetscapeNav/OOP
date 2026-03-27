package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.stage.Stage;
import model.Level;
import model.Obstacle;

import java.util.List;

public class MenuController {

    @FXML private Button startButton;
    @FXML private ComboBox<String> levelComboBox;
    @FXML private Spinner<Integer> widthSpinner;
    @FXML private Spinner<Integer> heightSpinner;
    @FXML private Spinner<Integer> foodCountSpinner;
    @FXML private Spinner<Integer> winLengthSpinner;
    @FXML private Spinner<Integer> speedSpinner;

    private List<Level> defaultLevels;

    @FXML
    private void initialize() {
        widthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 50, 20));
        heightSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 50, 15));
        foodCountSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 3));
        winLengthSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(5, 100, 10));
        speedSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(50, 500, 200));

        defaultLevels = Level.defaultLevels();

        levelComboBox.getItems().add("Custom");
        for (int i = 0; i < defaultLevels.size(); i++) {
            levelComboBox.getItems().add("Level " + (i + 1));
        }
        levelComboBox.setValue("Level 1");

        levelComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.equals("Custom")) {
                int levelIndex = levelComboBox.getSelectionModel().getSelectedIndex() - 1;
                if (levelIndex >= 0 && levelIndex < defaultLevels.size()) {
                    Level level = defaultLevels.get(levelIndex);
                    widthSpinner.getValueFactory().setValue(level.getWidth());
                    heightSpinner.getValueFactory().setValue(level.getHeight());
                    foodCountSpinner.getValueFactory().setValue(level.getFoodCount());
                    winLengthSpinner.getValueFactory().setValue(level.getWinLength());
                    speedSpinner.getValueFactory().setValue(level.getTickInterval());
                }
            }
        });
    }

    @FXML
    private void onStartClicked() {
        try {
            Level level = createLevelFromSettings();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/GameView.fxml"));
            Parent root = loader.load();

            GameController gameCtrl = loader.getController();
            gameCtrl.initGame(level);

            Stage stage = (Stage) startButton.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
                gameCtrl.handleKeyPress(event);
                event.consume();
            });
            stage.setScene(scene);
            stage.setTitle("Snake Game - Playing");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onExitClicked() {
        System.exit(0);
    }

    private Level createLevelFromSettings() {
        int width = widthSpinner.getValue();
        int height = heightSpinner.getValue();
        int tickInterval = speedSpinner.getValue();
        int foodCount = foodCountSpinner.getValue();
        int winLength = winLengthSpinner.getValue();

        return new Level(height, width, tickInterval, foodCount, winLength, new Obstacle(), 0);
    }
}