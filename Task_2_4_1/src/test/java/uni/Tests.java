package uni;

import check.Checker;
import check.Logger;
import checkservices.BuildService;
import checkservices.GitService;
import checkservices.StyleChecker;
import checkservices.TestReportParser;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApplicationTests {
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
        Date date = new Date();
        Checkpoint checkpoint = new Checkpoint(1, "КР1", date);

        assertEquals(1, checkpoint.getId());
        assertEquals("КР1", checkpoint.getName());
        assertEquals(date, checkpoint.getDate());
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

    @Test
    void testLogger() {
        assertDoesNotThrow(() -> Logger.info("Test message"));
        assertDoesNotThrow(() -> Logger.error("Test error", new RuntimeException("Test exception")));
        assertDoesNotThrow(() -> Logger.error("Test error without exception", null));
    }
    
    @Test
    void testServicesWithInvalidPaths() {
        File invalidDir = new File("non_existent_directory_12345");
        
        BuildService buildService = new BuildService(invalidDir);
        assertFalse(buildService.build("test", "1_1"));
        
        GitService gitService = new GitService(invalidDir);
        assertDoesNotThrow(() -> gitService.gitPull("test"));
        assertDoesNotThrow(() -> gitService.gitClone("invalid_url", "test"));
        assertEquals(java.time.LocalDate.now(), gitService.getLastCommitDate(invalidDir, "test", "1_1"));
        
        File newDir = new File("another_dir");
        gitService.setDir(newDir);
        assertEquals(newDir, gitService.getDir());
        
        StyleChecker styleChecker = new StyleChecker(invalidDir);
        assertEquals(0, styleChecker.revise("test", "1_1"));
        
        TestReportParser parser = new TestReportParser(invalidDir);
        int[] result = parser.run("test", "1_1");
        assertEquals(0, result[0]);
        assertEquals(0, result[1]);
    }
    
    @Test
    void testBuildServiceWithEmptyDir() {
        File emptyDir = new File("empty_test_dir");
        emptyDir.mkdir();
        
        BuildService buildService = new BuildService(emptyDir);
        assertFalse(buildService.build("test", "1_1"));
        
        emptyDir.delete();
    }
    
    @Test
    void testTestReportParserWithXml() throws Exception {
        File testDir = new File("test_report_dir");
        testDir.mkdirs();
        
        File reportsDir = new File(testDir, "build/test-results/test");
        reportsDir.mkdirs();
        
        String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<testsuite name=\"uni.Tests\" tests=\"5\" skipped=\"0\" failures=\"1\" errors=\"1\" timestamp=\"2024-05-24T12:00:00\" hostname=\"localhost\" time=\"0.01\">\n" +
                "</testsuite>";
        
        Files.writeString(new File(reportsDir, "TEST-uni.Tests.xml").toPath(), xmlContent);

        File gradlew = new File(testDir, "gradlew");
        Files.writeString(gradlew.toPath(), "#!/bin/sh\nexit 0\n");
        gradlew.setExecutable(true);

        File gradlewBat = new File(testDir, "gradlew.bat");
        Files.writeString(gradlewBat.toPath(), "@echo off\nexit /b 0\n");
        gradlewBat.setExecutable(true);
        
        TestReportParser parser = new TestReportParser(testDir);
        int[] result = parser.run("test", "1_1");

        assertEquals(3, result[0]);
        assertEquals(5, result[1]);

        new File(reportsDir, "TEST-uni.Tests.xml").delete();
        reportsDir.delete();
        new File(testDir, "build/test-results/test").delete();
        new File(testDir, "build/test-results").delete();
        new File(testDir, "build").delete();
        gradlew.delete();
        gradlewBat.delete();
        testDir.delete();
    }
    
    @Test
    void testGitServiceWithRealGit() throws Exception {
        File tempRepo = new File("temp_git_repo_for_test");
        tempRepo.mkdirs();

        ProcessBuilder pb = new ProcessBuilder("git", "init");
        pb.directory(tempRepo);
        pb.start().waitFor();
        
        GitService gitService = new GitService(tempRepo);

        assertDoesNotThrow(() -> gitService.gitPull("test"));

        File gitDir = new File(tempRepo, ".git");
        if (gitDir.exists()) {
            deleteDirectory(gitDir);
        }
        tempRepo.delete();
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
