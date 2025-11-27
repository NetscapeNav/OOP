import org.junit.jupiter.api.Test;
import table.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class testTable {
    @Test
    void testGetLastTwoExamSessions() {
        Table table = new Table();
        table.setCurrentSemester(5);
        table.addGrade(new Grade(new Subject("БД", GradeType.EXAM, 4), GradeScore.GOOD, true));
        table.addGrade(new Grade(new Subject("Физ-ра", GradeType.DIFF_CREDIT, 4), GradeScore.GOOD, true)); // Должно попасть в выборку!
        table.addGrade(new Grade(new Subject("Алгоритмы", GradeType.EXAM, 3), GradeScore.EXCELLENT, true));
        table.addGrade(new Grade(new Subject("МатАн", GradeType.EXAM, 2), GradeScore.SATISFACTORY, true));
        List<Grade> result = table.getLastTwoExamSessions();
        assertEquals(3, result.size());
        boolean hasDiffCredit = result.stream()
                .anyMatch(g -> g.getSubject().getType() == GradeType.DIFF_CREDIT);
        assertTrue(hasDiffCredit, "Дифф. зачет за сессионный семестр должен быть в списке");
    }

    @Test
    void testFutureSemesterProtection() {
        Table table = new Table();
        table.setCurrentSemester(2);
        assertThrows(IllegalArgumentException.class, () ->
                table.addGrade(new Grade(new Subject("Future", GradeType.EXAM, 3), GradeScore.GOOD, true))
        );
    }

    @Test
    void testUnmodifiableList() {
        Table table = new Table();
        List<Grade> grades = table.getGrades();
        assertThrows(UnsupportedOperationException.class, () ->
                grades.add(new Grade(new Subject("Hack", GradeType.EXAM, 1), GradeScore.GOOD, true))
        );
    }

    @Test
    void testFilterMethods() {
        Table table = new Table();
        table.setCurrentSemester(2);
        Grade g1 = new Grade(new Subject("Math 1", GradeType.EXAM, 1), GradeScore.GOOD, true);
        Grade g2 = new Grade(new Subject("Math 2", GradeType.EXAM, 2), GradeScore.GOOD, false);

        table.addGrade(g1);
        table.addGrade(g2);

        List<Grade> current = table.getCurrentSemesterGrades();
        assertEquals(1, current.size());
        assertEquals("Math 2", current.get(0).getSubject().getName());

        List<Grade> finals = table.getFinalGrades();
        assertEquals(1, finals.size());
        assertEquals("Math 1", finals.get(0).getSubject().getName());
    }

    @Test
    void testToString() {
        Table table = new Table();
        table.setCurrentSemester(1);
        assertTrue(table.toString().contains("Зачетка пуста"));

        table.addGrade(new Grade(new Subject("Test", GradeType.CREDIT, 1), GradeScore.GOOD, true));
        assertTrue(table.toString().contains("Test"));
    }
}