package check;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

import checkservices.BuildService;
import checkservices.GitService;
import checkservices.StyleChecker;
import checkservices.TestReportParser;
import uni.Config;

public class Checker {
    private final Config config;

    public Checker (Config config) {
        this.config = config;
    }

    public void check() {
        Logger.info("Чекер запущен для конфигурации");
        HTMLReporter reporter = new HTMLReporter();

        File baseDir = new File("student_repositories");
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }

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

                    boolean compiled = buildService.build(nick, taskID);
                    int styleScore = styleChecker.revise(nick, taskID);

                    if (compiled) {
                        if (styleScore == 1) {
                            Logger.info("[" + nick + " | Task_" + taskID + "] -> Стиль Task_" + taskID + " проверен (1 балл).");
                        } else {
                            Logger.info("[" + nick + " | Task_" + taskID + "] -> Стиль Task_" + taskID + " провалился (0 баллов).");
                        }

                        TestReportParser testReportParser = new TestReportParser(taskDir);

                        int[] testResults = testReportParser.run(nick, taskID);
                        int passed = testResults[0];
                        int total = testResults[1];

                        int finalScore = 0;
                        if (total > 0) {
                            double baseScore = task.getMaxScore() * ((double) passed / total);

                            LocalDate submissionDate = gitService.getLastCommitDate(taskDir, nick, taskID);

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

                        reporter.addRecord(
                                String.valueOf(group.getNumber()),
                                student.getGithubNick(),
                                taskID,
                                compiled,
                                passed,
                                total,
                                finalScore
                        );
                    } else {
                        reporter.addRecord(
                                String.valueOf(group.getNumber()),
                                student.getGithubNick(),
                                taskID,
                                false,
                                0,
                                0,
                                0
                        );
                    }
                });
            });
        });
        
        reporter.generateHTMLReport(config.getConversions()); 
    }
}
