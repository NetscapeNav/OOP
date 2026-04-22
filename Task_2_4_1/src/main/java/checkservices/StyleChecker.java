package checkservices;

import java.io.File;

public class StyleChecker {
    private File dir;

    public StyleChecker(File dir) {
        this.dir = dir;
    }

    public int revise(String studentNick, String taskId) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "java", "-jar", "../../lib/checkstyle-all.jar",
                    "-c", "/google_checks.xml",
                    "src/main/java"
            );
            pb.directory(dir);

            Process process = pb.start();
            return process.waitFor() == 0 ? 1 : 0;
        } catch (Exception e) {
            return 0;
        }
    }
}
