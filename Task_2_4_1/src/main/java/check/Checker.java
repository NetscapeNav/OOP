package check;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import checkservices.BuildService;
import checkservices.DocGenerator;
import checkservices.GitService;
import checkservices.StyleChecker;
import checkservices.TestReportParser;
import uni.Config;
import uni.Group;
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

        int globalTimeout = config.getTimeout();

        config.getGroups().parallelStream().forEach(group ->
                processGroup(group, baseDir, globalTimeout)
        );

        reporter.generateHTMLReport(config.getConversions(), config.getCheckpoints());
    }

    private void processGroup(Group group, File baseDir, int globalTimeout) {
        List<String> assignedTaskIds = config.getAssignedTasksForGroup(group.getNumber());

        if (assignedTaskIds == null || assignedTaskIds.isEmpty()) {
            Logger.info("[" + group.getNumber() + "] Для группы нет назначенных задач.");
            return;
        }

        group.getStudents().parallelStream().forEach(student ->
                processStudent(student, group, baseDir, globalTimeout, assignedTaskIds)
        );
    }

    private void processStudent(Student student, Group group, File baseDir,
                                int globalTimeout, List<String> assignedTaskIds) {
        String repository = student.getRepoURL();
        String nick = student.getGithubNick();

        if (repository == null || repository.isEmpty()) {
            Logger.info("[" + nick + "] У студента нет ссылки на репозиторий!");
            return;
        }

        File studentDIR = new File(baseDir, nick);
        updateOrCloneRepository(studentDIR, repository, nick);

        Logger.info("[" + nick + "] Ищем Java-файлы для компиляции...");

        config.getTasks().parallelStream()
                .filter(task -> assignedTaskIds.contains(task.getId()))
                .forEach(task -> processTask(task, student, group, studentDIR, globalTimeout));
    }

    private void updateOrCloneRepository(File studentDIR, String repository, String nick) {
        GitService gitService = new GitService(studentDIR);
        if (studentDIR.exists()) {
            Logger.info("[" + nick + "] Репозиторий уже скачан. Обновляем изменения (git pull)...");
            gitService.gitPull(nick);
        } else {
            Logger.info("[" + nick + "] Клонируем репозиторий для студента " + nick);
            gitService.gitClone(repository, nick);
        }
    }

    private void processTask(Task task, Student student, Group group, File studentDIR, int globalTimeout) {
        String taskID = task.getId();
        String nick = student.getGithubNick();
        File taskDir = new File(studentDIR, "Task_" + taskID);

        Logger.info("\n[" + nick + " | Task_" + taskID + "] Ищем и проверяем задачу Task_" + taskID + "...");

        BuildService buildService = new BuildService(taskDir);
        boolean compiled = buildService.build(nick, taskID, globalTimeout);
        LocalDate date = LocalDate.now();

        if (!compiled) {
            recordResult(group, nick, taskID, false, false, false, 0, 0, 0, date);
            return;
        }

        checkStyleAndDocs(task, student, group, taskDir, globalTimeout);
    }

    private void checkStyleAndDocs(Task task, Student student, Group group, File taskDir, int globalTimeout) {
        String taskID = task.getId();
        String nick = student.getGithubNick();

        StyleChecker styleChecker = new StyleChecker(taskDir);
        boolean stylePassed = (styleChecker.revise(nick, taskID, globalTimeout) == 1);
        logStyleResult(nick, taskID, stylePassed);

        DocGenerator docGenerator = new DocGenerator(taskDir);
        boolean docGenerated = docGenerator.generate(nick, taskID, globalTimeout);
        LocalDate date = LocalDate.now();

        if (!docGenerated) {
            Logger.info("[" + nick + " | Task_" + taskID + "] -> Документация не генерируется. Итог: 0 баллов.");
            recordResult(group, nick, taskID, true, stylePassed, false, 0, 0, 0, date);
            return;
        }

        processTestsAndScore(task, student, group, taskDir, globalTimeout, stylePassed);
    }

    private void processTestsAndScore(Task task, Student student, Group group, File taskDir,
                                      int timeout, boolean stylePassed) {
        String taskID = task.getId();
        String nick = student.getGithubNick();

        TestReportParser testReportParser = new TestReportParser(taskDir);
        int[] testResults = testReportParser.run(nick, taskID, timeout);
        int passed = testResults[0];
        int total = testResults[1];
        int finalScore = 0;
        LocalDate submissionDate = LocalDate.now();

        if (total > 0) {
            GitService gitService = new GitService(taskDir);
            submissionDate = gitService.getLastCommitDate(taskDir, nick, taskID);
            finalScore = calculateScore(task, nick, taskID, passed, total, submissionDate);
        }

        recordResult(group, nick, taskID, true, stylePassed, true, passed, total, finalScore, submissionDate);
    }

    private int calculateScore(Task task, String nick, String taskID, int passed, int total, LocalDate date) {
        double baseScore = task.getMaxScore() * ((double) passed / total);
        String soft = task.getSoftDeadline();
        String hard = task.getHardDeadline();

        if (hard != null && !hard.isEmpty() && date.isAfter(LocalDate.parse(hard))) {
            Logger.info("[" + nick + " | Task_" + taskID + "] -> Жесткий дедлайн (" + hard
                    + ") пропущен! Итог: 0 баллов.");
            return 0;
        }

        if (soft != null && !soft.isEmpty() && date.isAfter(LocalDate.parse(soft))) {
            int score = (int) Math.round(baseScore * 0.5);
            Logger.info("[" + nick + " | Task_" + taskID + "] -> Мягкий дедлайн (" + soft
                    + ") пропущен! Штраф 50%. Итог: " + score);
            return score;
        }

        int score = (int) Math.round(baseScore);
        Logger.info("[" + nick + " | Task_" + taskID + "] -> Сдано вовремя! (" + date + "). Штрафов нет.");
        return score;
    }

    private void logStyleResult(String nick, String taskID, boolean stylePassed) {
        if (stylePassed) {
            Logger.info("[" + nick + " | Task_" + taskID + "] -> Стиль Task_" + taskID + " проверен (1 балл).");
        } else {
            Logger.info("[" + nick + " | Task_" + taskID + "] -> Стиль Task_" + taskID + " провалился (0 баллов).");
        }
    }

    private void recordResult(Group group, String nick, String taskID, boolean compiled, boolean stylePassed,
                              boolean docGenerated, int passed, int total, int finalScore, LocalDate date) {
        reporter.addRecord(
                String.valueOf(group.getNumber()), nick, taskID, compiled,
                stylePassed, docGenerated, passed, total, finalScore, date
        );
    }

    public HTMLReporter getReporter() {
        return reporter;
    }
}