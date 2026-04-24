package checkservices;

import check.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DocGenerator {
    private final File dir;

    public DocGenerator(File dir) {
        this.dir = dir;
    }

    public boolean generate(String studentNick, String taskId, int timeout) {
        if (!dir.exists()) {
            Logger.info("[" + studentNick + " | Task_" + taskId + "] Папка не найдена, документация не может быть сгенерирована.");
            return false;
        }

        File gradleBat = new File(dir, "gradlew.bat");
        File gradleSh = new File(dir, "gradlew");

        if (gradleBat.exists() || gradleSh.exists()) {
            return generateWithGradle(studentNick, taskId, timeout);
        } else {
            return generateWithJavadoc(studentNick, taskId, timeout);
        }
    }

    private boolean generateWithGradle(String studentNick, String taskId, int timeout) {
        try {
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

            List<String> command = new ArrayList<>();
            if (isWindows) {
                command.add("cmd.exe");
                command.add("/c");
                command.add("gradlew.bat");
            } else {
                command.add("./gradlew");
            }
            command.add("javadoc");

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(dir);

            Process process = pb.start();
            boolean finished = process.waitFor(timeout, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                Logger.info("[" + studentNick + " | Task_" + taskId + "] Таймаут при генерации документации.");
                return false;
            }

            if (process.exitValue() == 0) {
                Logger.info("[" + studentNick + " | Task_" + taskId + "] Документация успешно сгенерирована.");
                return true;
            } else {
                Logger.info("[" + studentNick + " | Task_" + taskId + "] Ошибка генерации документации (" + process.exitValue() + ")");
                return false;
            }
        } catch (Exception e) {
            Logger.error("[" + studentNick + " | Task_" + taskId + "] Ошибка при генерации документации:", e);
            return false;
        }
    }

    private boolean generateWithJavadoc(String studentNick, String taskId, int timeout) {
        try {
            List<String> javaFiles = findJavaFiles(dir);
            if (javaFiles.isEmpty()) {
                Logger.info("[" + studentNick + " | Task_" + taskId + "] Нет Java-файлов для генерации документации.");
                return false;
            }

            File docOutput = new File(dir, "docs");
            docOutput.mkdirs();

            List<String> command = new ArrayList<>();
            command.add("javadoc");
            command.add("-d");
            command.add(docOutput.getAbsolutePath());
            command.add("-encoding");
            command.add("UTF-8");
            command.add("-quiet");
            command.addAll(javaFiles);

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(dir);
            pb.redirectErrorStream(true);

            Process process = pb.start();
            boolean finished = process.waitFor(timeout, TimeUnit.SECONDS);

            if (!finished) {
                process.destroyForcibly();
                Logger.info("[" + studentNick + " | Task_" + taskId + "] Таймаут при генерации документации.");
                return false;
            }

            if (process.exitValue() == 0) {
                Logger.info("[" + studentNick + " | Task_" + taskId + "] Документация успешно сгенерирована.");
                return true;
            } else {
                Logger.info("[" + studentNick + " | Task_" + taskId + "] Ошибка генерации документации (" + process.exitValue() + ")");
                return false;
            }
        } catch (Exception e) {
            Logger.error("[" + studentNick + " | Task_" + taskId + "] Ошибка при вызове javadoc:", e);
            return false;
        }
    }

    private List<String> findJavaFiles(File dir) {
        List<String> javaFiles = new ArrayList<>();
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory() && !file.getName().equals(".git") && !file.getName().equals("docs")) {
                    javaFiles.addAll(findJavaFiles(file));
                } else if (file.getName().endsWith(".java")) {
                    javaFiles.add(file.getAbsolutePath());
                }
            }
        }
        return javaFiles;
    }
}
