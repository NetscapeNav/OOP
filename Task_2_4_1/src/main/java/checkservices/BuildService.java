package checkservices;

import check.Logger;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BuildService {
    private File dir;

    public BuildService(File dir) {
        this.dir = dir;
    }

    public boolean build(String studentNick, String taskId) {
        return compileStudentCode(studentNick, taskId);
    }

    private boolean compileStudentCode(String studentNick, String taskId) {
        if (!dir.exists()) {
            Logger.info("[" + studentNick + " | Task_" + taskId + "] Папка " + dir.getName() + " не найдена!");
            return false;
        }

        File gradleBat = new File(dir, "gradlew.bat");
        File gradleSh = new File(dir, "gradlew");

        if (!gradleBat.exists() && !gradleSh.exists()) {
            Logger.info("[" + studentNick + " | Task_" + taskId + "] В папке " + dir.getName() + " нет Gradle Wrapper. Пытаемся собрать с помощью javac...");
            return compileWithJavac(studentNick, taskId);
        }

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

            command.add("classes");
            command.add("testClasses");

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(dir);
            
            Process process = pb.start();
            int code = process.waitFor();

            if (code == 0) {
                Logger.info("[" + studentNick + " | Task_" + taskId + "] Gradle успешно скомпилировал проект!");
                return true;
            } else {
                Logger.info("[" + studentNick + " | Task_" + taskId + "] Ошибка сборки Gradle (" + code + ")");
                return false;
            }
        } catch (Exception e) {
            Logger.info("[" + studentNick + " | Task_" + taskId + "] Критическая ошибка вызова Gradle:");
            Logger.error("", e);
            return false;
        }
    }

    private boolean compileWithJavac(String studentNick, String taskId) {
        List<String> javaFiles = findJavaFiles(dir);
        if (javaFiles.isEmpty()) return false;

        try {
            List<String> command = new ArrayList<>();
            command.add("javac");
            command.add("-encoding");
            command.add("UTF-8");
            command.addAll(javaFiles);

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(dir);

            pb.redirectErrorStream(true);

            Process process = pb.start();
            int code = process.waitFor();

            if (code == 0) {
                Logger.info("[" + studentNick + " | Task_" + taskId + "] Успешно скомпилировано с помощью javac.");
                return true;
            } else {
                Logger.info("[" + studentNick + " | Task_" + taskId + "] Ошибка компиляции javac (" + code + "). Возможно, не хватает зависимостей.");
                return false;
            }
        } catch (Exception e) {
            Logger.info("[" + studentNick + " | Task_" + taskId + "] Ошибка при вызове javac:");
            Logger.error("", e);
            return false;
        }
    }

    private List<String> findJavaFiles(File dir) {
        List<String> javaFiles = new ArrayList<>();
        File[] files = dir.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (!file.getName().equals(".git")) {
                        javaFiles.addAll(findJavaFiles(file));
                    }
                } else if (file.getName().endsWith(".java")) {
                    javaFiles.add(file.getAbsolutePath());
                }
            }
        }

        return javaFiles;
    }
}
