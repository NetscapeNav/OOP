package tests;

import check.Checker;
import check.HTMLReporter;
import check.Logger;
import checkservices.BuildService;
import checkservices.TestReportParser;
import org.junit.jupiter.api.Test;
import uni.*;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UniTests {
    @Test
    void testLogger() {
        assertDoesNotThrow(() -> Logger.info("Test message"));
        assertDoesNotThrow(() -> Logger.error("Test error", new RuntimeException("Test exception")));
        assertDoesNotThrow(() -> Logger.error("Test error without exception", null));
    }

    @Test
    void testTaskGettersAndSetters() {
        Task task = new Task();
        task.setId("test_id");
        task.setName("Test Task");
        task.setMaxScore(10);
        task.setSoftDeadline("2026-05-01");
        task.setHardDeadline("2026-05-15");

        assertAll("Task validation",
                () -> assertEquals("test_id", task.getId()),
                () -> assertEquals("Test Task", task.getName()),
                () -> assertEquals(10, task.getMaxScore()),
                () -> assertEquals("2026-05-01", task.getSoftDeadline()),
                () -> assertEquals("2026-05-15", task.getHardDeadline())
        );

        Task task2 = new Task("test_id2", "Test Task 2", "2026-06-01", "2026-06-15", 20);
        assertEquals("test_id2", task2.getId());
    }

    @Test
    void testGroupAndStudentGettersAndSetters() {
        Student student = new Student();
        student.setId(1);
        student.setName("Иван Иванов");
        student.setGithubNick("ivan");
        student.setRepoURL("http://github.com/ivan");

        assertAll("Student validation",
                () -> assertEquals(1, student.getId()),
                () -> assertEquals("Иван Иванов", student.getName()),
                () -> assertEquals("ivan", student.getGithubNick()),
                () -> assertEquals("http://github.com/ivan", student.getRepoURL())
        );

        Student student2 = new Student(2, "Петр Петров", "petr", "http://github.com/petr");
        assertEquals(2, student2.getId());

        Group group = new Group();
        group.setNumber(123);
        group.setStudents(List.of(student));
        assertEquals(123, group.getNumber());
        assertEquals(1, group.getStudents().size());
        assertEquals("ivan", group.getStudents().get(0).getGithubNick());

        Group group2 = new Group(456, List.of(student2));
        assertEquals(456, group2.getNumber());
    }

    @Test
    void testCheckpointGetters() {
        Checkpoint checkpoint = new Checkpoint("КР1", "2026-04-01");
        assertEquals("КР1", checkpoint.getName());
        assertEquals(java.time.LocalDate.parse("2026-04-01"), checkpoint.getDate());
    }

    @Test
    void testConfigParsing() throws Exception {
        String dummyConfig = "tasks {\n" +
                "    task('9_9') {\n" +
                "        name = 'Тестовая задача'\n" +
                "        maxScore = 100\n" +
                "    }\n" +
                "}\n" +
                "groups {\n" +
                "    group(100) {\n" +
                "        student('test_student') {\n" +
                "            name = 'Иван Иванов'\n" +
                "            repoURL = 'http://test.git'\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "assignments {\n" +
                "    assign '9_9' to 100\n" +
                "}\n" +
                "checkpoints {\n" +
                "    point 'КР1', '2026-04-01'\n" +
                "}\n" +
                "settings {\n" +
                "    timeout = 30\n" +
                "    styleGuide = 'GOOGLE'\n" +
                "    convertion 50, 4\n" +
                "}";

        Path configPath = Path.of("test_config.groovy");
        Files.writeString(configPath, dummyConfig);

        Config config = new Config();
        config.include("test_config.groovy");

        assertEquals(1, config.getTasks().size());
        Task task = config.getTasks().get(0);
        assertEquals("9_9", task.getId());
        assertEquals(100, task.getMaxScore());

        assertEquals(1, config.getGroups().size());
        Group group = config.getGroups().get(0);
        assertEquals(100, group.getNumber());
        assertEquals(1, group.getStudents().size());
        assertEquals("test_student", group.getStudents().get(0).getGithubNick());

        List<String> assignedTasks = config.getAssignedTasksForGroup(100);
        assertTrue(assignedTasks.contains("9_9"));

        assertEquals(1, config.getConversions().size());
        assertEquals(50, config.getConversions().get(0).getScore());
        assertEquals(4, config.getConversions().get(0).getMark());

        assertEquals(30, config.getTimeout());
        assertEquals("GOOGLE", config.getStyleGuide());

        assertEquals(1, config.getCheckpoints().size());
        assertEquals("КР1", config.getCheckpoints().get(0).getName());
        assertEquals(java.time.LocalDate.parse("2026-04-01"), config.getCheckpoints().get(0).getDate());

        Files.delete(configPath);
    }

}
