package tests;

import check.Checker;
import check.HTMLReporter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import uni.Config;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CheckerTest {

    @AfterEach
    void tearDown() {
        deleteDirectory(new File("student_repositories"));
        new File("report.html").delete();
        new File("lib/checkstyle-all.jar").delete();

        String[] configFiles = {
                "deadline_config.groovy", "deadline_config_2.groovy",
                "no_tasks_config.groovy", "wrong_group_config.groovy",
                "checkpoint_config.groovy", "checker_coverage_config.groovy"
        };
        for (String f : configFiles) {
            new File(f).delete();
        }
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
    }

    @Test
    void testCheckerVariousBranches() throws Exception {
        File baseDir = new File("student_repositories");
        baseDir.mkdirs();

        File existingStudentDir = new File(baseDir, "existing_student");
        existingStudentDir.mkdirs();

        File taskDir = new File(existingStudentDir, "Task_1");
        taskDir.mkdirs();

        File gradlew = new File(taskDir, "gradlew");
        Files.writeString(gradlew.toPath(), "#!/bin/sh\nif [ \"$1\" = \"javadoc\" ]; then exit 1; fi\nexit 0\n");
        gradlew.setExecutable(true);

        File gradlewBat = new File(taskDir, "gradlew.bat");
        Files.writeString(gradlewBat.toPath(), "@echo off\nif \"%1\"==\"javadoc\" exit /b 1\nexit /b 0\n");
        gradlewBat.setExecutable(true);

        String configContent = "tasks {\n" +
                "    task('1') { maxScore = 10 }\n" +
                "}\n" +
                "groups {\n" +
                "    group(1) {\n" +
                "        student('existing_student') { repoURL = 'http://git' }\n" +
                "        student('empty_repo_student') { repoURL = '' }\n" +
                "        student('null_repo_student') { }\n" +
                "    }\n" +
                "}\n" +
                "assignments {\n" +
                "    assign '1' to 1\n" +
                "}\n";

        Path configPath = Path.of("checker_coverage_config.groovy");
        Files.writeString(configPath, configContent);

        Config config = new Config();
        config.include("checker_coverage_config.groovy");

        Checker checker = new Checker(config);

        checker.check();
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
