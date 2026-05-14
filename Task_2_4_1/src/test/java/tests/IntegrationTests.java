package tests;

import org.example.Main;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTests {
    private final Path configPath = Path.of("config.groovy");
    private final File reportFile = new File("report.html");

    @BeforeEach
    void setUp() throws Exception {
        if (reportFile.exists()) {
            reportFile.delete();
        }

        String dummyConfig = "tasks {\n" +
                "    task('main_integration') {\n" +
                "        name = 'Main Task'\n" +
                "        maxScore = 10\n" +
                "    }\n" +
                "}\n" +
                "groups {\n" +
                "    group(999) {\n" +
                "        student('main_student') {\n" +
                "            name = 'Integration Student'\n" +
                "            repoURL = 'dummy_url'\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "assignments {\n" +
                "    assign 'main_integration' to 999\n" +
                "}";
        Files.writeString(configPath, dummyConfig);

        File dummyRepo = new File("student_repositories/main_student/Task_main_integration/src/main/java/test");
        dummyRepo.mkdirs();
        Files.writeString(new File(dummyRepo, "HelloWorld.java").toPath(),
                "package test; public class HelloWorld { public static void main(String[] args) {} }");
    }

    @AfterEach
    void tearDown() throws Exception {
        Files.deleteIfExists(configPath);
        if (reportFile.exists()) {
            reportFile.delete();
        }
        deleteDirectory(new File("student_repositories"));
    }

    @Test
    void testMainExecutionWithArgs() {
        assertDoesNotThrow(() -> Main.main(new String[]{"test"}));

        assertTrue(reportFile.exists(), "HTML отчет должен быть создан после вызова Main.main");
    }

    @Test
    void testMainExecutionWithoutArgs() {
        assertDoesNotThrow(() -> Main.main(new String[]{}));

        assertFalse(reportFile.exists(), "HTML отчет не должен создаваться без аргумента 'test'");
    }

    private void deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        directoryToBeDeleted.delete();
    }
}