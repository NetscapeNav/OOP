package checkservices;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import check.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestReportParser {
    private File dir;

    public TestReportParser(File dir) {
        this.dir = dir;
    }

    public int[] run(String studentNick, String taskId, int timeout) {
        int totalTests = 0;
        int failedTests = 0;

        try {
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            List<String> command = new ArrayList<>();
            File gradleBat = new File(dir, "gradlew.bat");
            File pom = new File(dir, "pom.xml");

            if (gradleBat.exists() || new File(dir, "gradlew").exists()) {
                if (isWindows) {
                    command.add("cmd.exe"); command.add("/c"); command.add("gradlew.bat");
                } else {
                    command.add("./gradlew");
                }
                command.add("test");
            } else if (pom.exists()) {
                command.add(isWindows ? "mvn.cmd" : "mvn");
                command.add("test");
            } else {
                Logger.info("[" + studentNick + " | Task_" + taskId + "] Неизвестная система сборки для тестов");
                return new int[]{0, 0};
            }

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(dir);
            Process process = pb.start();
            boolean finished = process.waitFor(timeout, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                Logger.info("[" + studentNick + " | Task_" + taskId + "] Таймаут выполнения тестов (" + timeout + " сек)");
                return new int[]{0, 0};
            }

            File gradleReports = new File(dir, "build/test-results/test");
            File mavenReports = new File(dir, "target/surefire-reports");
            File reportsDir = gradleReports.exists() ? gradleReports : mavenReports;

            if (reportsDir.exists() && reportsDir.isDirectory()) {
                File[] xmlFiles = reportsDir.listFiles((dir_unused, name) -> name.endsWith(".xml"));
                if (xmlFiles != null) {
                    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder builder = factory.newDocumentBuilder();

                    for (File xml : xmlFiles) {
                        Document doc = builder.parse(xml);
                        Element suite = doc.getDocumentElement();

                        totalTests += Integer.parseInt(suite.getAttribute("tests"));
                        failedTests += Integer.parseInt(suite.getAttribute("failures"));
                        failedTests += Integer.parseInt(suite.getAttribute("errors"));
                    }
                }
            } else {
                Logger.info("[" + studentNick + " | Task_" + taskId + "] Отсутствуют результаты тестов");
            }
        } catch (Exception e) {
            Logger.error("", e);
        }

        int passedTests = totalTests - failedTests;
        Logger.info("[" + studentNick + " | Task_" + taskId + "] -> Тесты: пройдено " + passedTests + " из " + totalTests);
        return new int[]{passedTests, totalTests};
    }
}
