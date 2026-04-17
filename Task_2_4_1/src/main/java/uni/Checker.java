package uni;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Checker {
    private Config config;

    public Checker (Config config) {
        this.config = config;
    }

    public void check() {
        System.out.println("Чекер запущен для конфигурации");
        HTMLReporter reporter = new HTMLReporter();

        File baseDir = new File("student_repositories");
        if (!baseDir.exists()) {
            baseDir.mkdir();
        }

        for (Group group : config.getGroups()) {
            for (Student student : group.getStudents()) {
                String repository = student.getRepoURL();

                if (repository == null || repository.isEmpty()) {
                    System.out.println("У студента " + student.getGithubNick() + " нет ссылки на репозиторий!");
                    continue;
                }

                File studentDIR = new File(baseDir, student.getGithubNick());
                if (studentDIR.exists()) {
                    System.out.println("Репозиторий " + student.getGithubNick() + " уже скачан. Обновляем изменения (git pull)...");
                    try {
                        ProcessBuilder pbReset = new ProcessBuilder("git", "reset", "--hard", "HEAD");
                        pbReset.directory(studentDIR);
                        pbReset.start().waitFor();

                        ProcessBuilder pbClean = new ProcessBuilder("git", "clean", "-fd");
                        pbClean.directory(studentDIR);
                        pbClean.start().waitFor();

                        ProcessBuilder pb = new ProcessBuilder("git", "pull");
                        pb.directory(studentDIR);
                        pb.inheritIO();
                        pb.start().waitFor();

                        checkoutMainOrMaster(studentDIR);
                    } catch (Exception e) {
                        System.out.println("Ошибка при обновлении репозитория:");
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("Клонируем репозиторий для студента " + student.getGithubNick());
                    try {
                        ProcessBuilder pb = new ProcessBuilder("git", "clone", repository, studentDIR.getAbsolutePath());
                        pb.inheritIO();
                        Process process = pb.start();
                        int code = process.waitFor();

                        if (code == 0) {
                            System.out.println("Успешно загружен репозиторий");
                        } else {
                            System.out.println("Ошибка при загрузке репозитория (" + code + ")");
                        }
                    } catch (Exception e) {
                        System.out.println("Критическая ошибка вызова git:");
                        e.printStackTrace();
                    }
                }

                System.out.println("Ищем Java-файлы для компиляции...");
                
                List<String> assignedTaskIds = config.getAssignedTasksForGroup(group.getNumber());
                
                if (assignedTaskIds == null || assignedTaskIds.isEmpty()) {
                    System.out.println("Для группы " + group.getNumber() + " нет назначенных задач.");
                    continue; 
                }
                
                for (Task task : config.getTasks()) {
                    String taskID = task.getId();
                    
                    if (!assignedTaskIds.contains(taskID)) {
                        continue;
                    }
                    
                    System.out.println("\nИщем и проверяем задачу Task_" + taskID + "...");
                    File taskDir = new File(studentDIR, "Task_" + taskID);

                    boolean compiled = compileStudentCode(taskDir);
                    int styleScore = checkStyle(taskDir);

                    if (compiled) {
                        System.out.println("-> Сборка Task_" + taskID + " успешна!");
                        if (styleScore == 1) {
                            System.out.println("-> Стиль Task_" + taskID + " проверен (1 балл).");
                        } else {
                            System.out.println("-> Стиль Task_" + taskID + " провалился (0 баллов).");
                        }

                        int[] testResults = runTests(taskDir);
                        int passed = testResults[0];
                        int total = testResults[1];

                        System.out.println("--- ИТОГ ПО ЗАДАЧЕ: " + passed + "/" + total + " тестов ---");
                        int finalScore = 0;
                        if (total > 0) {
                            double baseScore = task.getMaxScore() * ((double) passed / total);

                            LocalDate submissionDate = getLastCommitDate(taskDir);

                            String soft = task.getSoftDeadline();
                            String hard = task.getHardDeadline();

                            if (hard != null && !hard.isEmpty() && submissionDate.isAfter(LocalDate.parse(hard))) {
                                finalScore = 0;
                                System.out.println("-> Жесткий дедлайн (" + hard + ") пропущен! Дата сдачи: " + submissionDate + ". Итог: 0 баллов.");
                            } else if (soft != null && !soft.isEmpty() && submissionDate.isAfter(LocalDate.parse(soft))) {
                                finalScore = (int) Math.round(baseScore * 0.5);
                                System.out.println("-> Мягкий дедлайн (" + soft + ") пропущен! Дата сдачи: " + submissionDate + ". Штраф 50%. Итог: " + finalScore);
                            } else {
                                finalScore = (int) Math.round(baseScore);
                                System.out.println("-> Сдано вовремя! (" + submissionDate + "). Штрафов нет.");
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
                        System.out.println("-> Сборка Task_" + taskID + " провалилась (0 баллов).");
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
                }
            }
        }
        
        reporter.generateHTMLReport(config.getConversions()); 
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
            System.out.println("Ошибка при переключении веток.");
        }
    }

    private boolean compileStudentCode(File taskDir) {
        if (!taskDir.exists()) {
            System.out.println("Папка " + taskDir.getName() + " не найдена!");
            return false;
        }

        File gradleBat = new File(taskDir, "gradlew.bat");
        File gradleSh = new File(taskDir, "gradlew");

        if (!gradleBat.exists() && !gradleSh.exists()) {
            System.out.println("В папке " + taskDir.getName() + " нет Gradle Wrapper. Пытаемся собрать с помощью javac...");
            return compileWithJavac(taskDir);
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
            pb.directory(taskDir);
            pb.inheritIO();

            Process process = pb.start();
            int code = process.waitFor();

            if (code == 0) {
                System.out.println("Gradle успешно скомпилировал проект!");
                return true;
            } else {
                System.out.println("Ошибка сборки Gradle (" + code + ")");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Критическая ошибка вызова Gradle:");
            e.printStackTrace();
            return false;
        }
    }

    private boolean compileWithJavac(File studentDIR) {
        List<String> javaFiles = findJavaFiles(studentDIR);
        if (javaFiles.isEmpty()) return false;

        try {
            List<String> command = new ArrayList<>();
            command.add("javac");
            command.add("-encoding");
            command.add("UTF-8");
            command.addAll(javaFiles);

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(studentDIR);

            pb.redirectErrorStream(true);
            
            Process process = pb.start();
            int code = process.waitFor();

            if (code == 0) {
                System.out.println("Успешно скомпилировано с помощью javac.");
                return true;
            } else {
                System.out.println("Ошибка компиляции javac (" + code + "). Возможно, не хватает зависимостей.");
                return false;
            }
        } catch (Exception e) {
            System.out.println("Ошибка при вызове javac:");
            e.printStackTrace();
            return false;
        }
    }

    private List<String> findJavaFiles(File studentDIR) {
        List<String> javaFiles = new ArrayList<>();
        File[] files = studentDIR.listFiles();

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

    private int checkStyle(File taskDIR) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "java", "-jar", "../../lib/checkstyle-all.jar",
                    "-c", "/google_checks.xml",
                    "src/main/java"
            );
            pb.directory(taskDIR);

            Process process = pb.start();
            return process.waitFor() == 0 ? 1 : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    private int[] runTests(File taskDIR) {
        int totalTests = 0;
        int failedTests = 0;

        try {
            boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
            List<String> command = new ArrayList<>();
            File gradleBat = new File(taskDIR, "gradlew.bat");
            File pom = new File(taskDIR, "pom.xml");

            if (gradleBat.exists() || new File(taskDIR, "gradlew").exists()) {
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
                System.out.println("Неизвестная система сборки для тестов");
                return new int[]{0, 0};
            }

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(taskDIR);
            pb.start().waitFor();

            File gradleReports = new File(taskDIR, "build/test-results/test");
            File mavenReports = new File(taskDIR, "target/surefire-reports");
            File reportsDir = gradleReports.exists() ? gradleReports : mavenReports;

            if (reportsDir.exists() && reportsDir.isDirectory()) {
                File[] xmlFiles = reportsDir.listFiles((dir, name) -> name.endsWith(".xml"));
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
                System.out.println("Отсутствуют результаты тестов");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        int passedTests = totalTests - failedTests;
        System.out.println("-> Тесты: пройдено " + passedTests + " из " + totalTests);
        return new int[]{passedTests, totalTests};
    }

    private LocalDate getLastCommitDate(File taskDir) {
        try {
            ProcessBuilder pb = new ProcessBuilder("git", "log", "-1", "--format=%cd", "--date=short", ".");
            pb.directory(taskDir);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String dateStr = reader.readLine();
            process.waitFor();

            if (dateStr != null && !dateStr.trim().isEmpty()) {
                System.out.println("-> Дата последней сдачи (коммита): " + dateStr.trim());
                return LocalDate.parse(dateStr.trim());
            }
        } catch (Exception e) {
            System.out.println("Не удалось получить дату коммита, используем текущую дату.");
        }
        return LocalDate.now();
    }
}
