import org.junit.jupiter.api.Test;
import table.GradeType;
import table.Subject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class testSubject {
    @Test
    void testValidSubject() {
        Subject subject = new Subject("Математика", GradeType.EXAM, 1);
        assertEquals("Математика", subject.getName());
        assertEquals(GradeType.EXAM, subject.getType());
        assertEquals(1, subject.getSemester());
    }

    @Test
    void testInvalidName() {
        assertThrows(IllegalArgumentException.class, () ->
                new Subject(null, GradeType.EXAM, 1)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Subject("", GradeType.EXAM, 1)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Subject("Math#1", GradeType.EXAM, 1)
        );
    }

    @Test
    void testInvalidSemester() {
        assertThrows(IllegalArgumentException.class, () ->
                new Subject("Физика", GradeType.EXAM, 0)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Subject("Физика", GradeType.EXAM, -1)
        );
    }
}