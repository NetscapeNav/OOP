package checkservices;

import check.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.LocalDate;

public class GitService {
    private File dir;

    public GitService(File dir) {
        this.dir = dir;
    }

    public void gitPull(String studentNick) {
        try {
            ProcessBuilder pbReset = new ProcessBuilder("git", "reset", "--hard", "HEAD");
            pbReset.directory(dir);
            pbReset.start().waitFor();

            ProcessBuilder pbClean = new ProcessBuilder("git", "clean", "-fd");
            pbClean.directory(dir);
            pbClean.start().waitFor();

            ProcessBuilder pb = new ProcessBuilder("git", "pull");
            pb.directory(dir);
            pb.start().waitFor();

            checkoutMainOrMaster(dir);
            Logger.info("[" + studentNick + "] Репозиторий успешно обновлен.");
        } catch (Exception e) {
            Logger.info("[" + studentNick + "] Ошибка при обновлении репозитория:");
            Logger.error("", e);
        }
    }

    public void gitClone(String repository, String studentNick) {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "clone", repository, dir.getAbsolutePath());
            Process process = pb.start();
            int code = process.waitFor();
            if (code == 0) {
                Logger.info("[" + studentNick + "] Успешно загружен репозиторий");
            } else {
                Logger.info("[" + studentNick + "] Ошибка при загрузке репозитория (" + code + ")");
            }
        } catch (Exception e) {
            Logger.info("[" + studentNick + "] Критическая ошибка вызова git:");
            Logger.error("", e);
        }
    }

    private void checkoutMainOrMaster(File studentDIR) {
        try {
            ProcessBuilder pbMain = new ProcessBuilder("git", "checkout", "main");
            pbMain.directory(studentDIR);
            if (pbMain.start().waitFor() != 0) {
                ProcessBuilder pbMaster = new ProcessBuilder("git", "checkout", "master");
                pbMaster.directory(studentDIR);
                pbMaster.start().waitFor();
            }
        } catch (Exception e) {
            Logger.info("Ошибка при переключении веток.");
        }
    }

    public LocalDate getLastCommitDate(File taskDir, String studentNick, String taskId) {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "log", "-1", "--format=%cd", "--date=short", ".");
            pb.directory(taskDir);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String dateStr = reader.readLine();
            process.waitFor();

            if (dateStr != null && !dateStr.trim().isEmpty()) {
                Logger.info("[" + studentNick + " | Task_" + taskId + "] -> Дата последней сдачи (коммита): " + dateStr.trim());
                return LocalDate.parse(dateStr.trim());
            }
        } catch (Exception e) {
            Logger.info("[" + studentNick + " | Task_" + taskId + "] Не удалось получить дату коммита, используем текущую дату.");
        }
        return LocalDate.now();
    }

    public File getDir() {
        return dir;
    }

    public void setDir(File dir) {
        this.dir = dir;
    }
}
