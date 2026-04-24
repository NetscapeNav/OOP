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

public class Checker {
    private final Config config;
    private final HTMLReporter reporter;

    public Checker (Config config) {
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

        config.getGroups().parallelStream().forEach(group -> {
            group.getStudents().parallelStream().forEach(student -> {
                String repository = student.getRepoURL();
                String nick = student.getGithubNick();

                if (repository == null || repository.isEmpty()) {
                    Logger.info("[" + nick + "] У студента нет ссылки на репозиторий!");
                    return; 
                }

                File studentDIR = new File(baseDir, nick);
                GitService gitService = new GitService(studentDIR);

                if (studentDIR.exists()) {
                    Logger.info("[" + nick + "] Репозиторий уже скачан. Обновляем изменения (git pull)...");
                    gitService.gitPull(nick); 
                } else {
                    Logger.info("[" + nick + "] Клонируем репозиторий для студента " + nick);
                    gitService.gitClone(repository, nick); 
                }

                Logger.info("[" + nick + "] Ищем Java-файлы для компиляции...");
                
                List<String> assignedTaskIds = config.getAssignedTasksForGroup(group.getNumber());
                
                if (assignedTaskIds == null || assignedTaskIds.isEmpty()) {
                    Logger.info("[" + nick + "] Для группы " + group.getNumber() + " нет назначенных задач.");
                    return;
                }

                config.getTasks().parallelStream().forEach(task -> {
                    String taskID = task.getId();
                    
                    if (!assignedTaskIds.contains(taskID)) {
                        return;
                    }

                    Logger.info("\n[" + nick + " | Task_" + taskID + "] Ищем и проверяем задачу Task_" + taskID + "...");
                    File taskDir = new File(studentDIR, "Task_" + taskID);

                    BuildService buildService = new BuildService(taskDir); 
                    StyleChecker styleChecker = new StyleChecker(taskDir); 
                    DocGenerator docGenerator = new DocGenerator(taskDir);
                    TestReportParser testReportParser = new TestReportParser(taskDir);

                    boolean compiled = buildService.build(nick, taskID, globalTimeout);
                    boolean stylePassed = false;
                    boolean docGenerated = false;

                    int passed = 0;
                    int total = 0;
                    int finalScore = 0;
                    LocalDate submissionDate = LocalDate.now();

                    if (compiled) {
                        stylePassed = (styleChecker.revise(nick, taskID, globalTimeout) == 1);
                        if (stylePassed) {
                            Logger.info("[" + nick + " | Task_" + taskID + "] -> Стиль Task_" + taskID + " проверен (1 балл).");
                        } else {
                            Logger.info("[" + nick + " | Task_" + taskID + "] -> Стиль Task_" + taskID + " провалился (0 баллов).");
                        }

                        docGenerated = docGenerator.generate(nick, taskID, globalTimeout);

                        if (docGenerated) {
                            int[] testResults = testReportParser.run(nick, taskID, globalTimeout);
                            passed = testResults[0];
                            total = testResults[1];

                            if (total > 0) {
                                double baseScore = task.getMaxScore() * ((double) passed / total);

                                submissionDate = gitService.getLastCommitDate(taskDir, nick, taskID);

                                String soft = task.getSoftDeadline();
                                String hard = task.getHardDeadline();

                                if (hard != null && !hard.isEmpty() && submissionDate.isAfter(LocalDate.parse(hard))) {
                                    finalScore = 0;
                                    Logger.info("[" + nick + " | Task_" + taskID + "] -> Жесткий дедлайн (" + hard + ") пропущен! Итог: 0 баллов.");
                                } else if (soft != null && !soft.isEmpty() && submissionDate.isAfter(LocalDate.parse(soft))) {
                                    finalScore = (int) Math.round(baseScore * 0.5);
                                    Logger.info("[" + nick + " | Task_" + taskID + "] -> Мягкий дедлайн (" + soft + ") пропущен! Штраф 50%. Итог: " + finalScore);
                                } else {
                                    finalScore = (int) Math.round(baseScore);
                                    Logger.info("[" + nick + " | Task_" + taskID + "] -> Сдано вовремя! (" + submissionDate + "). Штрафов нет.");
                                }
                            }
                        } else {
                            Logger.info("[" + nick + " | Task_" + taskID + "] -> Документация не генерируется. Итог: 0 баллов.");
                        }

                        reporter.addRecord(
                                String.valueOf(group.getNumber()),
                                student.getGithubNick(),
                                taskID,
                                compiled,
                                stylePassed,
                                docGenerated,
                                passed,
                                total,
                                finalScore,
                                submissionDate
                        );
                    } else {
                        reporter.addRecord(
                                String.valueOf(group.getNumber()),
                                student.getGithubNick(),
                                taskID,
                                false,
                                false,
                                false,
                                0,
                                0,
                                0,
                                submissionDate
                        );
                    }
                });
            });
        });
        
        reporter.generateHTMLReport(config.getConversions(), config.getCheckpoints()); 
    }

    public HTMLReporter getReporter() {
        return reporter;
    }
}
