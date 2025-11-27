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
}