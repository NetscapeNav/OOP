package uni;

import check.Checker;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Tests {
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
                "settings {\n" +
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

        Files.delete(configPath);
    }

    @Test
    void testSystemIntegration() throws Exception {
        File dummyRepo = new File("student_repositories/test_student_integration/Task_test_integration");
        dummyRepo.mkdirs();

        File srcDir = new File(dummyRepo, "src/main/java/test");
        srcDir.mkdirs();
        File javaFile = new File(srcDir, "HelloWorld.java");
        Files.writeString(javaFile.toPath(), "package test; public class HelloWorld { public static void main(String[] args) {} }");

        String dummyConfig = "tasks {\n" +
                "    task('test_integration') {\n" +
                "        name = 'Integration Task'\n" +
                "        maxScore = 10\n" +
                "    }\n" +
                "}\n" +
                "groups {\n" +
                "    group(999) {\n" +
                "        student('test_student_integration') {\n" +
                "            name = 'Integration Student'\n" +
                "            repoURL = 'dummy_url'\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "assignments {\n" +
                "    assign 'test_integration' to 999\n" +
                "}";

        Path configPath = Path.of("integration_config.groovy");
        Files.writeString(configPath, dummyConfig);

        Config config = new Config();
        config.include("integration_config.groovy");

        Checker checker = new Checker(config);

        assertDoesNotThrow(() -> checker.check());

        File report = new File("report.html");
        assertTrue(report.exists(), "HTML отчет должен быть создан");

        Files.deleteIfExists(configPath);
    }
}
