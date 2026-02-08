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
        assertEquals(GradeScore.SATISFACTORY, grade.getScore());
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
    void testCopyConstructor() {
        Subject subject = new Subject("Тест", GradeType.EXAM, 2);
        Grade original = new Grade(subject, GradeScore.EXCELLENT, false);

        Grade copy = new Grade(original);

        assertEquals(original.getScore(), copy.getScore());
        assertEquals(original.getSubject(), copy.getSubject());
        assertEquals(original.isFinal(), copy.isFinal());
        assertEquals(original.getDate(), copy.getDate());
        assertNotSame(original.getDate(), copy.getDate());
    }

    @Test
    void testToString() {
        Subject subject = new Subject("Физика", GradeType.EXAM, 1);
        Grade grade = new Grade(subject, GradeScore.GOOD, true);

        String s = grade.toString();
        assertTrue(s.contains("Физика"));
        assertTrue(s.contains("Экзамен"));
        assertTrue(s.contains("Хорошо"));
    }

    @Test
    void testNullArguments() {
        Subject subject = new Subject("Тест", GradeType.CREDIT, 1);
        assertThrows(IllegalArgumentException.class, () -> new Grade(null, GradeScore.GOOD, true));
        assertThrows(IllegalArgumentException.class, () -> new Grade(subject, null, true));
    }
}
