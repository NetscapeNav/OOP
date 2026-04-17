package uni;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    private Task task;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId("test_id");
        task.setName("Test Task");
        task.setMaxScore(10);
    }

    @Test
    void testTaskGetters() {
        assertEquals("test_id", task.getId(), "ID должен совпадать");
        assertEquals("Test Task", task.getName(), "Имя должно совпадать");
        assertEquals(10, task.getMaxScore(), "Максимальный балл должен быть 10");
    }

    @Test
    void testDeadlineSetting() {
        task.setSoftDeadline("2026-05-01");
        task.setHardDeadline("2026-05-15");

        assertAll("Deadlines",
                () -> assertEquals("2026-05-01", task.getSoftDeadline()),
                () -> assertEquals("2026-05-15", task.getHardDeadline())
        );
    }
}