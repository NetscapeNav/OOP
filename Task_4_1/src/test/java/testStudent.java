import org.junit.jupiter.api.Test;
import table.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class testStudent {
    @Test
    void testBuilderValidation() {
        assertThrows(IllegalArgumentException.class, () ->
                new Student.Builder("", "Иванов", "Информатика", StudyForm.BUDGET)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Student.Builder("Иван", "", "Информатика", StudyForm.BUDGET)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Student.Builder("Иван", "Иванов", "", StudyForm.BUDGET)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Student.Builder("Иван123", "Иванов", "Информатика", StudyForm.BUDGET)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Student.Builder("Иван", "Иванов", "Информатика", null)
        );
    }

    @Test
    void testSettersAndGetters() {
        Student student = new Student.Builder("Иван", "Иванов", "IT", StudyForm.PAID)
                .middleName("Иванович")
                .qualifyingWorkGrade(4)
                .build();

        assertEquals("Иван", student.getFirstName());
        assertEquals("Иванов", student.getLastName());
        assertEquals("Иванович", student.getMiddleName());
        assertEquals("IT", student.getSpeciality());
        assertEquals(StudyForm.PAID, student.getStudyForm());
        assertEquals(4, student.getQualifyingWorkGrade());

        student.setFirstName("Петр");
        assertEquals("Петр", student.getFirstName());
        assertThrows(IllegalArgumentException.class, () -> student.setFirstName(""));

        student.setLastName("Петров");
        assertEquals("Петров", student.getLastName());
        assertThrows(IllegalArgumentException.class, () -> student.setLastName(""));

        student.setMiddleName("Петрович");
        assertEquals("Петрович", student.getMiddleName());

        student.setSpeciality("Математика");
        assertEquals("Математика", student.getSpeciality());

        student.setStudyForm(StudyForm.BUDGET);
        assertEquals(StudyForm.BUDGET, student.getStudyForm());

        student.setQualifyingWorkGrade(5);
        assertEquals(5, student.getQualifyingWorkGrade());
        assertThrows(IllegalArgumentException.class, () -> student.setQualifyingWorkGrade(6));
    }

    @Test
    void testBudgetTransferLogic() {
        Student student = new Student.Builder("Иван", "Иванов", "Информатика", StudyForm.PAID).build();
        Table t = student.getTranscript();
        t.setCurrentSemester(3);

        t.addGrade(new Grade(new Subject("Математика", GradeType.EXAM, 1), GradeScore.EXCELLENT, true));
        t.addGrade(new Grade(new Subject("Физическая культура", GradeType.DIFF_CREDIT, 2), GradeScore.SATISFACTORY, true));
        t.addGrade(new Grade(new Subject("Программирование", GradeType.EXAM, 2), GradeScore.GOOD, true));

        assertTrue(student.canTransferToBudget(), "Должен перевестись с тройкой по зачету");

        t.addGrade(new Grade(new Subject("Философия", GradeType.EXAM, 2), GradeScore.SATISFACTORY, true));
        assertFalse(student.canTransferToBudget(), "Не должен переводиться с тройкой по экзамену");

        student.setStudyForm(StudyForm.BUDGET);
        assertFalse(student.canTransferToBudget(), "Бюджетник не может перевестись на бюджет");
    }

    @Test
    void testRedDiploma() {
        Student student = new Student.Builder("Иван", "Иванов", "Информатика", StudyForm.BUDGET)
                .qualifyingWorkGrade(5)
                .build();
        Table t = student.getTranscript();
        t.setCurrentSemester(8);

        t.addGrade(new Grade(new Subject("С1", GradeType.EXAM, 1), GradeScore.EXCELLENT, true));
        t.addGrade(new Grade(new Subject("С2", GradeType.EXAM, 2), GradeScore.EXCELLENT, true));
        assertTrue(student.canGetRedDiploma());

        t.addGrade(new Grade(new Subject("С3", GradeType.EXAM, 3), GradeScore.SATISFACTORY, true));
        assertFalse(student.canGetRedDiploma());

        student.setTranscript(new Table());
        assertFalse(student.canGetRedDiploma());
    }

    @Test
    void testScholarship() {
        Student student = new Student.Builder("Пётр", "Петров", "Информатика", StudyForm.BUDGET).build();
        Table t = student.getTranscript();
        t.setCurrentSemester(2);

        student.setTranscript(null);
        assertFalse(student.canGetScholarship());
        assertFalse(student.canGetIncreasedScholarship());
        student.setTranscript(t);

        t.addGrade(new Grade(new Subject("Математика", GradeType.EXAM, 2), GradeScore.EXCELLENT, true));
        assertTrue(student.canGetScholarship(), "Отличник должен получать стипендию");
        assertTrue(student.canGetIncreasedScholarship(), "Отличник должен получать повышенную");

        t.addGrade(new Grade(new Subject("История", GradeType.EXAM, 2), GradeScore.GOOD, true));
        assertTrue(student.canGetScholarship(), "Хорошист должен получать стипендию");
        assertFalse(student.canGetIncreasedScholarship(), "Хорошист НЕ должен получать повышенную");

        t.addGrade(new Grade(new Subject("Физика", GradeType.EXAM, 2), GradeScore.SATISFACTORY, true));
        assertFalse(student.canGetScholarship(), "Троечник не получает стипендию");
    }

    @Test
    void testFullReport() {
        Student student = new Student.Builder("Мария", "Иванова", "Биология", StudyForm.PAID)
                .middleName("Ивановна")
                .build();

        Table t = student.getTranscript();
        t.setCurrentSemester(1);
        t.addGrade(new Grade(new Subject("Зоология", GradeType.EXAM, 1), GradeScore.EXCELLENT, true));

        String report = student.getFullReport();

        assertNotNull(report);
        assertTrue(report.contains("Иванова Мария Ивановна"));
        assertTrue(report.contains("Биология"));
        assertTrue(report.contains("Зоология"));
        assertTrue(report.contains("Отлично"));

        student.setStudyForm(StudyForm.BUDGET);
        String budgetReport = student.getFullReport();
        assertTrue(budgetReport.contains("уже на бюджете"));
    }

    @Test
    void testAverageScoreEdges() {
        Student student = new Student.Builder("А", "Б", "В", StudyForm.PAID).build();
        assertEquals(0.0f, student.averageScore());
    }
}
