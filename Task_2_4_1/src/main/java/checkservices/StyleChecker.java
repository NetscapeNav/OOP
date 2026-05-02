package checkservices;

import check.Logger;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class StyleChecker {
    private final File dir;

    public StyleChecker(File dir) {
        this.dir = dir;
    }

    public int revise(String studentNick, String taskId, int timeout) {
        try {
            String checkstylePath = new File(System.getProperty("user.dir"), "lib/checkstyle-all.jar").getAbsolutePath();

            ProcessBuilder pb = new ProcessBuilder(
                    "java", "-jar", checkstylePath,
                    "-c", "/google_checks.xml",
                    "src/main/java"
            );
            pb.directory(dir);

            Process process = pb.start();
            boolean finished = process.waitFor(timeout, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                Logger.info("[" + studentNick + " | Task_" + taskId + "] Таймаут при проверке стиля.");
                return 0;
            }

            return process.exitValue() == 0 ? 1 : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
