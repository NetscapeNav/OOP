package check;

import uni.Convertion;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HTMLReporter {
    private List<Record> records = java.util.Collections.synchronizedList(new ArrayList<>());
    
    private static class Record {
        String groupName;
        String studentName;
        String taskId;
        boolean isCompiled;
        int passedTests;
        int totalTests;
        int finalScore;

        Record(String groupName, String studentName, String taskId, boolean isCompiled, int passedTests, int totalTests, int finalScore) {
            this.groupName = groupName;
            this.studentName = studentName;
            this.taskId = taskId;
            this.isCompiled = isCompiled;
            this.passedTests = passedTests;
            this.totalTests = totalTests;
            this.finalScore = finalScore;
        }
    }

    public void addRecord(String groupName, String studentName, String taskId, boolean isCompiled, int passedTests, int totalTests, int finalScore) {
        records.add(new Record(groupName, studentName, taskId, isCompiled, passedTests, totalTests, finalScore));
    }
    
    public void generateHTMLReport(List<Convertion> conversions) {
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
        html.append("<tr><th>Группа</th><th>Студент</th><th>Задача</th><th>Сборка</th><th>Checkstyle</th><th>Тесты</th><th>Общий балл</th></tr>");

        java.util.Map<String, Integer> studentTotalScores = new java.util.HashMap<>();

        synchronized (records) {
            for (Record record : records) {
                String buildStatus = record.isCompiled ? "+" : "-";
                String styleStatus = "-";

                html.append("<tr>")
                        .append("<td>").append(record.groupName).append("</td>")
                        .append("<td>").append(record.studentName).append("</td>")
                        .append("<td>Task_").append(record.taskId).append("</td>")
                        .append("<td>").append(buildStatus).append("</td>")
                        .append("<td>").append(styleStatus).append("</td>")
                        .append("<td>").append(record.passedTests).append("/").append(record.totalTests).append("</td>")
                        .append("<td><b>").append(record.finalScore).append("</b></td>")
                        .append("</tr>");

                studentTotalScores.put(record.studentName, studentTotalScores.getOrDefault(record.studentName, 0) + record.finalScore);
            }
        }

        html.append("</table>");

        html.append("<h3>Итоговые оценки студентов:</h3><ul>");

        List<String> sortedStudents = new ArrayList<>(studentTotalScores.keySet());
        java.util.Collections.sort(sortedStudents);
        
        for (String student : sortedStudents) {
            int totalScore = studentTotalScores.get(student);
            int finalMark = 2;

            if (conversions != null) {
                for (Convertion conv : conversions) {
                    if (totalScore >= conv.getScore()) {
                        finalMark = Math.max(finalMark, conv.getMark());
                    }
                }
            }

            html.append("<li>Студент <b>").append(student)
                    .append("</b> — Сумма баллов: ").append(totalScore)
                    .append(" -> Оценка: <b>").append(finalMark).append("</b></li>");
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
}
