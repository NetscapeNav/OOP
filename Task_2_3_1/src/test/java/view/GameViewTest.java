package view;

import javafx.scene.canvas.Canvas;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

class GameViewTest {
    @Test
    void testConstructor() {
        try {
            Canvas canvas = new Canvas(800, 600);
            GameView view = new GameView(canvas, 20);
            assertNotNull(view);
        } catch (IllegalStateException e) {
            fail();
        }
    }
}
