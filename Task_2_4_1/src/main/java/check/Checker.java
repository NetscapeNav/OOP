package check;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import checkservices.*;
import uni.Config;
import uni.Student;
import uni.Task;

public class Checker {
    private final Config config;
    private final HTMLReporter reporter;

    public Checker(Config config) {
        this.config = config;
        this.reporter = new HTMLReporter();
    }

    public void check() {
        Logger.info("Чекер запущен для конфигурации");

        File baseDir = new File("student_repositories");
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }

        int timeout = config.getTimeout();

        config.getGroups().forEach(group -> {
            String groupNumber = String.valueOf(group.getNumber());
            List<String> assignedTaskIds = config.getAssignedTasksForGroup(group.getNumber());

            if (assignedTaskIds == null || assignedTaskIds.isEmpty()) {
                Logger.info("Для группы " + groupNumber + " нет назначенных задач.");
                return;
            }

            group.getStudents().parallelStream().forEach(student ->
                    processStudent(student, baseDir, groupNumber, assignedTaskIds, timeout)
            );
        });

        reporter.generateHTMLReport(config.getConversions(), config.getCheckpoints());
    }

    private void processStudent(Student student, File baseDir, String groupNumber,
                                List<String> assignedTaskIds, int timeout) {
        String repo = student.getRepoURL();
        String nick = student.getGithubNick();

        if (repo == null || repo.isEmpty()) {
            Logger.info("[" + nick + "] У студента нет ссылки на репозиторий!");
            return;
        }

        File studentDir = new File(baseDir, nick);
        GitService git = new GitService(studentDir);

        if (studentDir.exists()) {
            Logger.info("[" + nick + "] Репозиторий уже скачан. Обновляем (git pull)...");
            git.gitPull(nick);
        } else {
            Logger.info("[" + nick + "] Клонируем репозиторий...");
            git.gitClone(repo, nick);
        }

        Logger.info("[" + nick + "] Ищем Java-файлы для компиляции...");

        config.getTasks().parallelStream().forEach(task -> {
            String taskId = task.getId();
            if (!assignedTaskIds.contains(taskId)) {
                return;
            }
            processTask(task, nick, studentDir, groupNumber, timeout, git);
        });
    }

    private void processTask(Task task, String nick, File studentDir,
                             String groupNumber, int timeout, GitService git) {
        String taskId = task.getId();
        Logger.info("\n[" + nick + " | Task_" + taskId + "] Ищем и проверяем...");
        File taskDir = new File(studentDir, "Task_" + taskId);

        BuildService buildService = new BuildService(taskDir);
        boolean compiled = buildService.build(nick, taskId, timeout);

        if (compiled) {
            handleSuccessfulCompilation(task, nick, taskDir, taskId, groupNumber, timeout, git);
        } else {
            reporter.addRecord(groupNumber, nick, taskId, false, false, false, 0, 0, 0, LocalDate.now());
        }
    }

    private void handleSuccessfulCompilation(Task task, String nick, File taskDir,
                                             String taskId, String groupNumber, int timeout, GitService git) {
        StyleChecker styleChecker = new StyleChecker(taskDir);
        DocGenerator docGen = new DocGenerator(taskDir);
        TestReportParser testParser = new TestReportParser(taskDir);

        boolean styleOk = (styleChecker.revise(nick, taskId, timeout) == 1);
        logStyleResult(nick, taskId, styleOk);

        boolean docGenerated = docGen.generate(nick, taskId, timeout);
        if (!docGenerated) {
            Logger.info("[" + nick + " | Task_" + taskId + "] Документация не сгенерирована. Итог: 0 баллов.");
            reporter.addRecord(groupNumber, nick, taskId, true, styleOk, false, 0, 0, 0, LocalDate.now());
            return;
        }

        int[] testResults = testParser.run(nick, taskId, timeout);
        int passed = testResults[0];
        int total = testResults[1];
        int finalScore = 0;
        LocalDate submissionDate = git.getLastCommitDate(taskDir, nick, taskId);

        if (total > 0) {
            finalScore = calculateScore(task, passed, total, submissionDate, nick, taskId);
        }

        reporter.addRecord(groupNumber, nick, taskId, true, styleOk, true, passed, total, finalScore, submissionDate);
    }

    private int calculateScore(Task task, int passed, int total,
                               LocalDate submissionDate, String nick, String taskId) {
        double baseScore = task.getMaxScore() * ((double) passed / total);
        String softDeadline = task.getSoftDeadline();
        String hardDeadline = task.getHardDeadline();

        if (isAfter(hardDeadline, submissionDate)) {
            Logger.info("[" + nick + " | Task_" + taskId + "] Жёсткий дедлайн пропущен! Итог: 0 баллов.");
            return 0;
        }
        if (isAfter(softDeadline, submissionDate)) {
            int score = (int) Math.round(baseScore * 0.5);
            Logger.info("[" + nick + " | Task_" + taskId + "] Мягкий дедлайн пропущен. Штраф 50%. Итог: " + score);
            return score;
        }
        Logger.info("[" + nick + " | Task_" + taskId + "] Сдано вовремя! Штрафов нет.");
        return (int) Math.round(baseScore);
    }

    private boolean isAfter(String deadlineStr, LocalDate date) {
        if (deadlineStr == null || deadlineStr.isEmpty()) {
            return false;
        }
        return date.isAfter(LocalDate.parse(deadlineStr));
    }

    private void logStyleResult(String nick, String taskId, boolean ok) {
        if (ok) {
            Logger.info("[" + nick + " | Task_" + taskId + "] Стиль проверен (1 балл).");
        } else {
            Logger.info("[" + nick + " | Task_" + taskId + "] Стиль провален (0 баллов).");
        }
    }

    public HTMLReporter getReporter() { return reporter; }
}