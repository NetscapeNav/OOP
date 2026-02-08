package org.example;

import table.*;

public class Main {
    public static void main(String[] args) {
        Student student = Student.builder()
                .firstName("Ирина")
                .lastName("Власова")
                .speciality("Информатика")
                .studyForm(StudyForm.PAID)
                .middleName("Георгиевна")
                .qualifyingWorkGrade(5)
                .build();

        Table transcript = student.getTranscript();
        transcript.setCurrentSemester(5);

        fillTranscript(transcript);

        System.out.println(student.getFullReport());
    }

    private static void fillTranscript(Table t) {
        // --- 1 СЕМЕСТР (Давние оценки не должны влиять на текущую стипендию) ---
        t.addGrade(new Grade(
                new Subject("Математика 1", GradeType.EXAM, 1),
                GradeScore.EXCELLENT, true
        ));
        t.addGrade(new Grade(
                new Subject("История", GradeType.CREDIT, 1),
                GradeScore.SATISFACTORY, true // Тройка давно, не должна мешать
        ));

        // --- 2 СЕМЕСТР ---
        t.addGrade(new Grade(
                new Subject("Математика 2", GradeType.EXAM, 2),
                GradeScore.EXCELLENT, true
        ));
        t.addGrade(new Grade(
                new Subject("Программирование", GradeType.DIFF_CREDIT, 2),
                GradeScore.GOOD, true
        ));

        // --- 3 СЕМЕСТР (Сессия №1 для проверки бюджета) ---
        t.addGrade(new Grade(
                new Subject("Алгоритмы", GradeType.EXAM, 3),
                GradeScore.EXCELLENT, true
        ));
        t.addGrade(new Grade(
                new Subject("Философия", GradeType.EXAM, 3),
                GradeScore.GOOD, true
        ));

        // --- 4 СЕМЕСТР (Сессия №2 для проверки бюджета) ---
        // Есть тройка, но она за дифзачет!
        // По условию задачи с такой тройкой МОЖНО на бюджет.
        t.addGrade(new Grade(
                new Subject("Базы данных", GradeType.EXAM, 4),
                GradeScore.GOOD, true
        ));
        t.addGrade(new Grade(
                new Subject("Сети", GradeType.EXAM, 4),
                GradeScore.EXCELLENT, true
        ));
        t.addGrade(new Grade(
                new Subject("Физкультура", GradeType.DIFF_CREDIT, 4),
                GradeScore.SATISFACTORY, true // 3 за дифзачет (допустимо для бюджета)
        ));

        // --- 5 СЕМЕСТР (Текущий) ---
        t.addGrade(new Grade(
                new Subject("ИИ", GradeType.EXAM, 5),
                GradeScore.EXCELLENT, false // Ещё не сдал окончательно
        ));
    }
}