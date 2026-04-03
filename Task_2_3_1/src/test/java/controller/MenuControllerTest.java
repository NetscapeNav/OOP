package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MenuControllerTest {

    @Test
    void testControllerInitialization() {
        assertNotNull(getClass().getResource("/view/MenuView.fxml"));
    }
}
