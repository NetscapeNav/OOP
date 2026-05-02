package check;

import uni.Convertion;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HTMLReporter {
    private List<Record> records = java.util.Collections.synchronizedList(new ArrayList<>());
    
    public static class Record {
        public String groupName;
        public String studentName;
        public String taskId;
        public boolean isCompiled;
        public boolean stylePassed;
        public boolean docGenerated;
        public int passedTests;
        public int totalTests;
        public int finalScore;
        public LocalDate submissionDate;

        Record(String groupName, String studentName, String taskId, boolean isCompiled, boolean stylePassed, boolean docGenerated, int passedTests, int totalTests, int finalScore, LocalDate submissionDate) {
            this.groupName = groupName;
            this.studentName = studentName;
            this.taskId = taskId;
            this.isCompiled = isCompiled;
            this.stylePassed = stylePassed;
            this.docGenerated = docGenerated;
            this.passedTests = passedTests;
            this.totalTests = totalTests;
            this.finalScore = finalScore;
            this.submissionDate = submissionDate;
        }
    }

    public void addRecord(String groupName, String studentName, String taskId, boolean isCompiled, boolean stylePassed, boolean docGenerated, int passedTests, int totalTests, int finalScore, LocalDate submissionDate) {
        records.add(new Record(groupName, studentName, taskId, isCompiled, stylePassed, docGenerated, passedTests, totalTests, finalScore, submissionDate));
    }
    
    public List<Record> getRecords() {
        return new ArrayList<>(records);
    }
    
    public void generateHTMLReport(List<Convertion> conversions, List<uni.Checkpoint> checkpoints) {
        synchronized (records) {
            records.sort(Comparator
                    .comparing((Record r) -> r.groupName)
                    .thenComparing(r -> r.studentName)
                    .thenComparing(r -> r.taskId));
        }

        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta charset='UTF-8'>")
            .append("<style>table {width: 50%; border-collapse: collapse;} th, td {border: 1px solid black; padding: 8px; text-align: center;}</style>")
            .append("</head><body>");

        html.append("<h2>Сводный отчет по курсу ООП</h2>");
        html.append("<table>");
        html.append("<tr><th>Группа</th><th>Студент</th><th>Задача</th><th>Сборка</th><th>Стиль</th><th>Документация</th><th>Тесты</th><th>Общий балл</th></tr>");

        java.util.Map<String, Integer> studentTotalScores = new java.util.HashMap<>();

        synchronized (records) {
            for (Record record : records) {
                String buildStatus = record.isCompiled ? "+" : "-";
                String styleStatus = record.stylePassed ? "+" : "-";
                String docStatus = record.docGenerated ? "+" : "-";

                html.append("<tr>")
                        .append("<td>").append(record.groupName).append("</td>")
                        .append("<td>").append(record.studentName).append("</td>")
                        .append("<td>Task_").append(record.taskId).append("</td>")
                        .append("<td>").append(buildStatus).append("</td>")
                        .append("<td>").append(styleStatus).append("</td>")
                        .append("<td>").append(docStatus).append("</td>")
                        .append("<td>").append(record.passedTests).append("/").append(record.totalTests).append("</td>")
                        .append("<td><b>").append(record.finalScore).append("</b></td>")
                        .append("</tr>");

                studentTotalScores.put(record.studentName, studentTotalScores.getOrDefault(record.studentName, 0) + record.finalScore);
            }
        }

        html.append("</table>");

        html.append("<h3>Оценки студентов:</h3><ul>");

        List<String> sortedStudents = new ArrayList<>(studentTotalScores.keySet());
        java.util.Collections.sort(sortedStudents);
        
        for (String student : sortedStudents) {
            html.append("<li>Студент <b>").append(student).append("</b><ul>");

            if (checkpoints != null && !checkpoints.isEmpty()) {
                for (uni.Checkpoint checkpoint : checkpoints) {
                    int cpScore = 0;
                    synchronized (records) {
                        for (Record r : records) {
                            if (r.studentName.equals(student) && r.submissionDate != null && !r.submissionDate.isAfter(checkpoint.getDate())) {
                                cpScore += r.finalScore;
                            }
                        }
                    }
                    int cpMark = calculateMark(cpScore, conversions);
                    html.append("<li>Контрольная точка <b>").append(checkpoint.getName()).append("</b> (до ").append(checkpoint.getDate()).append(") — Баллов: ").append(cpScore).append(" -> Оценка: ").append(cpMark).append("</li>");
                }
            }

            int totalScore = studentTotalScores.get(student);
            int finalMark = calculateMark(totalScore, conversions);

            html.append("<li><b>Итоговая аттестация</b> — Сумма баллов: ").append(totalScore)
                    .append(" -> Оценка: <b>").append(finalMark).append("</b></li>");
                    
            html.append("</ul></li><br>");
        }
        html.append("</ul>");

        html.append("</body></html>");

        try {
            Files.writeString(Path.of("report.html"), html.toString());
            Logger.info(html.toString());
            Logger.info("\nОтчёт также сохранен в файл report.html");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int calculateMark(int score, List<Convertion> conversions) {
        int finalMark = 2;
        if (conversions != null) {
            for (Convertion conv : conversions) {
                if (score >= conv.getScore()) {
                    finalMark = Math.max(finalMark, conv.getMark());
                }
            }
        }
        return finalMark;
    }
}
