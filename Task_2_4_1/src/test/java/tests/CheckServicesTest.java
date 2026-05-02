package tests;

import checkservices.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.*;

public class CheckServicesTest {
    @Test
    void testBuildServiceWithEmptyDir() {
        File emptyDir = new File("empty_test_dir");
        emptyDir.mkdir();

        BuildService buildService = new BuildService(emptyDir);
        assertFalse(buildService.build("test", "1_1"));

        emptyDir.delete();
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

        deleteDirectory(testDir);
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

        deleteDirectory(testDir);
    }

    @Test
    void testBuildServiceJavacFail() throws Exception {
        File dir = new File("test_build_javac_fail");
        dir.mkdirs();
        File src = new File(dir, "src/main/java/test");
        src.mkdirs();

        File javaFile = new File(src, "Bad.java");
        Files.writeString(javaFile.toPath(), "public class Bad { invalid syntax }");

        BuildService bs = new BuildService(dir);
        assertFalse(bs.build("test", "1_1", 10), "Должен вернуть false при ошибке компиляции javac");

        deleteDirectory(dir);
    }

    @Test
    void testBuildServiceGradleFail() throws Exception {
        File dir = new File("test_build_gradle_fail");
        dir.mkdirs();

        File gradlew = new File(dir, "gradlew");
        Files.writeString(gradlew.toPath(), "#!/bin/sh\nexit 1\n");
        gradlew.setExecutable(true);

        File gradlewBat = new File(dir, "gradlew.bat");
        Files.writeString(gradlewBat.toPath(), "@echo off\nexit /b 1\n");
        gradlewBat.setExecutable(true);

        BuildService bs = new BuildService(dir);
        assertFalse(bs.build("test", "1_1", 10), "Должен вернуть false при ошибке сборки Gradle");

        deleteDirectory(dir);
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
    void testGitServiceWithRealGit() throws Exception {
        File tempRepo = new File("temp_git_repo_for_test");
        tempRepo.mkdirs();

        ProcessBuilder pb = new ProcessBuilder("git", "init");
        pb.directory(tempRepo);
        pb.start().waitFor();

        GitService gitService = new GitService(tempRepo);

        assertDoesNotThrow(() -> gitService.gitPull("test"));

        deleteDirectory(tempRepo);
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
    void testTestReportParserNoBuildSystem() {
        File dir = new File("test_no_build_system");
        dir.mkdirs();

        TestReportParser parser = new TestReportParser(dir);
        int[] res = parser.run("test", "1_1", 10);

        assertEquals(0, res[0], "Должен вернуть 0 пройденных тестов, если нет системы сборки");
        assertEquals(0, res[1], "Должен вернуть 0 всего тестов, если нет системы сборки");

        deleteDirectory(dir);
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
