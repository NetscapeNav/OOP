package uni;

import check.Checker;
import check.HTMLReporter;
import check.Logger;
import checkservices.BuildService;
import checkservices.DocGenerator;
import checkservices.GitService;
import checkservices.StyleChecker;
import checkservices.TestReportParser;
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
        checker.check();

        File report = new File("report.html");
        assertTrue(report.exists(), "HTML отчет должен быть создан");
        
        List<HTMLReporter.Record> records = checker.getReporter().getRecords();
        assertEquals(1, records.size(), "Должна быть одна запись в отчете");
        assertEquals("999", records.get(0).groupName);
        assertEquals("test_student_integration", records.get(0).studentName);
        assertTrue(records.get(0).isCompiled, "Код должен быть скомпилирован");
        assertTrue(records.get(0).docGenerated, "Документация должна быть сгенерирована javadoc'ом");

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
        assertEquals(0, styleChecker.revise("test", "1_1", 10));
        
        TestReportParser parser = new TestReportParser(invalidDir);
        int[] result = parser.run("test", "1_1", 10);
        assertEquals(0, result[0]);
        assertEquals(0, result[1]);
        
        DocGenerator docGen = new DocGenerator(invalidDir);
        assertFalse(docGen.generate("test", "1_1", 10));
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
    void testTestReportParserWithEmptyDir() {
        File emptyDir = new File("empty_test_dir_2");
        emptyDir.mkdir();
        
        TestReportParser parser = new TestReportParser(emptyDir);
        int[] result = parser.run("test", "1_1", 10);
        assertEquals(0, result[0]);
        assertEquals(0, result[1]);
        
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
        int[] result = parser.run("test", "1_1", 10);

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
    
    @Test
    void testCheckerDeadlines() throws Exception {
        File dummyRepo = new File("student_repositories/test_deadlines/Task_1_1");
        dummyRepo.mkdirs();

        File srcDir = new File(dummyRepo, "src/main/java/test");
        srcDir.mkdirs();
        File javaFile = new File(srcDir, "HelloWorld.java");
        Files.writeString(javaFile.toPath(), "package test; public class HelloWorld { public static void main(String[] args) {} }");

        String dummyConfig = "tasks {\n" +
                "    task('1_1') {\n" +
                "        name = 'Integration Task'\n" +
                "        maxScore = 10\n" +
                "        softDeadline = '2020-01-01'\n" + 
                "        hardDeadline = '2022-01-01'\n" + 
                "    }\n" +
                "}\n" +
                "groups {\n" +
                "    group(888) {\n" +
                "        student('test_deadlines') {\n" +
                "            name = 'Deadline Student'\n" +
                "            repoURL = 'dummy_url'\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "assignments {\n" +
                "    assign '1_1' to 888\n" +
                "}";

        Path configPath = Path.of("deadline_config.groovy");
        Files.writeString(configPath, dummyConfig);

        Config config = new Config();
        config.include("deadline_config.groovy");

        Checker checker = new Checker(config);
        
        File libDir = new File("lib");
        libDir.mkdirs();
        File mockCheckstyle = new File(libDir, "checkstyle-all.jar");
        mockCheckstyle.createNewFile();
        
        checker.check();

        List<HTMLReporter.Record> records = checker.getReporter().getRecords();
        assertEquals(1, records.size());
        assertEquals(0, records.get(0).finalScore);

        Files.deleteIfExists(configPath);
        mockCheckstyle.delete();
    }
    
    @Test
    void testCheckerSoftDeadline() throws Exception {
        File dummyRepo = new File("student_repositories/test_deadlines/Task_1_2");
        dummyRepo.mkdirs();

        File srcDir = new File(dummyRepo, "src/main/java/test");
        srcDir.mkdirs();
        File javaFile = new File(srcDir, "HelloWorld.java");
        Files.writeString(javaFile.toPath(), "package test; public class HelloWorld { public static void main(String[] args) {} }");

        String dummyConfig = "tasks {\n" +
                "    task('1_2') {\n" +
                "        name = 'Integration Task'\n" +
                "        maxScore = 10\n" +
                "        softDeadline = '2020-01-01'\n" + 
                "        hardDeadline = '2040-01-01'\n" + 
                "    }\n" +
                "}\n" +
                "groups {\n" +
                "    group(888) {\n" +
                "        student('test_deadlines') {\n" +
                "            name = 'Deadline Student'\n" +
                "            repoURL = 'dummy_url'\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "assignments {\n" +
                "    assign '1_2' to 888\n" +
                "}";

        Path configPath = Path.of("deadline_config_2.groovy");
        Files.writeString(configPath, dummyConfig);

        Config config = new Config();
        config.include("deadline_config_2.groovy");

        Checker checker = new Checker(config);
        checker.check();

        List<HTMLReporter.Record> records = checker.getReporter().getRecords();
        assertEquals(1, records.size());
        
        Files.deleteIfExists(configPath);
    }

    @Test
    void testCheckerNoAssignedTasks() throws Exception {
        File baseDir = new File("student_repositories");
        baseDir.mkdirs();
        
        File dummyRepo = new File(baseDir, "test_no_tasks");
        dummyRepo.mkdirs();

        String dummyConfig = "tasks {\n" +
                "    task('1_3') {\n" +
                "        name = 'Integration Task'\n" +
                "        maxScore = 10\n" +
                "    }\n" +
                "}\n" +
                "groups {\n" +
                "    group(777) {\n" +
                "        student('test_no_tasks') {\n" +
                "            name = 'Student'\n" +
                "            repoURL = 'dummy_url'\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "assignments {\n" +
                "}\n"; 

        Path configPath = Path.of("no_tasks_config.groovy");
        Files.writeString(configPath, dummyConfig);

        Config config = new Config();
        config.include("no_tasks_config.groovy");

        Checker checker = new Checker(config);
        checker.check();
        
        List<HTMLReporter.Record> records = checker.getReporter().getRecords();
        assertEquals(0, records.size(), "Студенту не назначены задачи, отчет должен быть пустым");

        Files.deleteIfExists(configPath);
    }

    @Test
    void testCheckerTaskNotAssignedToGroup() throws Exception {
        File baseDir = new File("student_repositories");
        baseDir.mkdirs();
        
        File dummyRepo = new File(baseDir, "test_wrong_group");
        dummyRepo.mkdirs();

        String dummyConfig = "tasks {\n" +
                "    task('1_4') {\n" +
                "        name = 'Integration Task'\n" +
                "        maxScore = 10\n" +
                "    }\n" +
                "    task('1_5') {\n" +
                "        name = 'Other Task'\n" +
                "        maxScore = 10\n" +
                "    }\n" +
                "}\n" +
                "groups {\n" +
                "    group(666) {\n" +
                "        student('test_wrong_group') {\n" +
                "            name = 'Student'\n" +
                "            repoURL = 'dummy_url'\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "assignments {\n" +
                "    assign '1_5' to 666\n" + 
                "}\n"; 

        Path configPath = Path.of("wrong_group_config.groovy");
        Files.writeString(configPath, dummyConfig);

        Config config = new Config();
        config.include("wrong_group_config.groovy");

        Checker checker = new Checker(config);
        checker.check();

        List<HTMLReporter.Record> records = checker.getReporter().getRecords();
        assertTrue(records.stream().noneMatch(r -> r.taskId.equals("1_4")), "Задача 1_4 не должна быть в отчете, так как назначена только 1_5");

        Files.deleteIfExists(configPath);
    }

    @Test
    void testCheckerCheckpoints() throws Exception {
        File dummyRepo = new File("student_repositories/test_checkpoints/Task_2_1");
        dummyRepo.mkdirs();

        File srcDir = new File(dummyRepo, "src/main/java/test");
        srcDir.mkdirs();
        File javaFile = new File(srcDir, "HelloWorld.java");
        Files.writeString(javaFile.toPath(), "package test; public class HelloWorld { public static void main(String[] args) {} }");

        String dummyConfig = "tasks {\n" +
                "    task('2_1') {\n" +
                "        name = 'Integration Task'\n" +
                "        maxScore = 15\n" +
                "    }\n" +
                "}\n" +
                "groups {\n" +
                "    group(111) {\n" +
                "        student('test_checkpoints') {\n" +
                "            name = 'Checkpoint Student'\n" +
                "            repoURL = 'dummy_url'\n" +
                "        }\n" +
                "    }\n" +
                "}\n" +
                "assignments {\n" +
                "    assign '2_1' to 111\n" +
                "}\n" +
                "checkpoints {\n" +
                "    point 'КР1', '2050-01-01'\n" + 
                "    point 'Прошедшая', '2000-01-01'\n" +
                "}\n";

        Path configPath = Path.of("checkpoint_config.groovy");
        Files.writeString(configPath, dummyConfig);

        Config config = new Config();
        config.include("checkpoint_config.groovy");

        Checker checker = new Checker(config);
        checker.check();

        List<HTMLReporter.Record> records = checker.getReporter().getRecords();
        assertEquals(1, records.size());
        
        Files.deleteIfExists(configPath);
    }
    
    @Test
    void testBuildServiceWithGradleMock() throws Exception {
        File testDir = new File("test_build_dir");
        testDir.mkdirs();
        
        File gradlew = new File(testDir, "gradlew");
        Files.writeString(gradlew.toPath(), "#!/bin/sh\nexit 0\n");
        gradlew.setExecutable(true);

        File gradlewBat = new File(testDir, "gradlew.bat");
        Files.writeString(gradlewBat.toPath(), "@echo off\nexit /b 0\n");
        gradlewBat.setExecutable(true);
        
        BuildService buildService = new BuildService(testDir);
        assertTrue(buildService.build("test", "1_1"));
        
        gradlew.delete();
        gradlewBat.delete();
        testDir.delete();
    }
    
    @Test
    void testBuildServiceWithJavacMock() throws Exception {
        File testDir = new File("test_javac_dir");
        testDir.mkdirs();
        
        File srcDir = new File(testDir, "src/main/java/test");
        srcDir.mkdirs();
        File javaFile = new File(srcDir, "HelloWorld.java");
        Files.writeString(javaFile.toPath(), "package test; public class HelloWorld { public static void main(String[] args) {} }");
        
        BuildService buildService = new BuildService(testDir);
        assertTrue(buildService.build("test", "1_1"));
        
        javaFile.delete();
        srcDir.delete();
        new File(testDir, "src/main/java").delete();
        new File(testDir, "src/main").delete();
        new File(testDir, "src").delete();
        testDir.delete();
    }

    @Test
    void testDocGeneratorWithGradleMockSuccess() throws Exception {
        File testDir = new File("test_doc_gradle_dir");
        testDir.mkdirs();

        File gradlew = new File(testDir, "gradlew");
        Files.writeString(gradlew.toPath(), "#!/bin/sh\nexit 0\n");
        gradlew.setExecutable(true);

        File gradlewBat = new File(testDir, "gradlew.bat");
        Files.writeString(gradlewBat.toPath(), "@echo off\nexit /b 0\n");
        gradlewBat.setExecutable(true);

        DocGenerator docGen = new DocGenerator(testDir);
        assertTrue(docGen.generate("test", "1_1", 10), "Документация должна сгенерироваться через Gradle mock");

        deleteDirectory(testDir);
    }

    @Test
    void testDocGeneratorWithGradleMockFailure() throws Exception {
        File testDir = new File("test_doc_gradle_fail_dir");
        testDir.mkdirs();

        File gradlew = new File(testDir, "gradlew");
        Files.writeString(gradlew.toPath(), "#!/bin/sh\nexit 1\n");
        gradlew.setExecutable(true);

        File gradlewBat = new File(testDir, "gradlew.bat");
        Files.writeString(gradlewBat.toPath(), "@echo off\nexit /b 1\n");
        gradlewBat.setExecutable(true);

        DocGenerator docGen = new DocGenerator(testDir);
        assertFalse(docGen.generate("test", "1_1", 10), "Должен вернуть false при ошибке работы Gradle");

        deleteDirectory(testDir);
    }

    @Test
    void testDocGeneratorNoJavaFiles() throws Exception {
        File testDir = new File("test_doc_no_java_dir");
        testDir.mkdirs();

        DocGenerator docGen = new DocGenerator(testDir);
        assertFalse(docGen.generate("test", "1_1", 10), "Должен вернуть false, так как нет Java-файлов");

        deleteDirectory(testDir);
    }

    @Test
    void testDocGeneratorWithJavadocSuccess() throws Exception {
        File testDir = new File("test_javadoc_dir");
        testDir.mkdirs();

        File srcDir = new File(testDir, "src/main/java/test");
        srcDir.mkdirs();
        File javaFile = new File(srcDir, "HelloWorld.java");
        Files.writeString(javaFile.toPath(),
                "package test;\n" +
                        "/**\n * Test class\n */\n" +
                        "public class HelloWorld {\n" +
                        "    public static void main(String[] args) {}\n" +
                        "}");

        DocGenerator docGen = new DocGenerator(testDir);
        assertTrue(docGen.generate("test", "1_1", 10), "Документация должна успешно сгенерироваться через стандартный javadoc");

        deleteDirectory(testDir);
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
