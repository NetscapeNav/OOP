import org.junit.jupiter.api.Test;
import table.Grade;
import table.GradeScore;
import table.GradeType;
import table.Subject;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class testGrade {
    @Test
    void testGradeCreation() {
        Subject subject = new Subject("История", GradeType.CREDIT, 1);
        Grade grade = new Grade(subject, GradeScore.SATISFACTORY, true);
        assertEquals(3, grade.getIntScore());
        assertEquals("История", grade.getSubject().getName());
        assertTrue(grade.isFinal());
    }

    @Test
    void testDateImmutability() {
        Subject subject = new Subject("Тест", GradeType.CREDIT, 1);
        Grade grade = new Grade(subject, GradeScore.GOOD, true);
        Date d1 = grade.getDate();
        d1.setTime(0);
        Date d2 = grade.getDate();
        assertNotEquals(d1.getTime(), d2.getTime());
    }

    @Test
    void testNullArguments() {
        Subject subject = new Subject("Тест", GradeType.CREDIT, 1);
        assertThrows(IllegalArgumentException.class, () -> new Grade(null, GradeScore.GOOD, true));
        assertThrows(IllegalArgumentException.class, () -> new Grade(subject, null, true));
    }
}
