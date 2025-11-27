import org.junit.jupiter.api.Test;
import table.*;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class testStudent {
    @Test
    void testBuilderValidation() {
        assertThrows(IllegalArgumentException.class, () ->
                new Student.Builder("", "Ivanov", "IT", StudyForm.BUDGET)
        );
        assertThrows(IllegalArgumentException.class, () ->
                new Student.Builder("Ivan123", "Ivanov", "IT", StudyForm.BUDGET)
        );
    }

    @Test
    void testBudgetTransferLogic() {
        Student student = new Student.Builder("Ivan", "Ivanov", "IT", StudyForm.PAID).build();
        Table t = student.getTranscript();
        t.setCurrentSemester(3);
        t.addGrade(new Grade(new Subject("Math", GradeType.EXAM, 1), GradeScore.EXCELLENT, true));
        t.addGrade(new Grade(new Subject("PE", GradeType.DIFF_CREDIT, 2), GradeScore.SATISFACTORY, true));
        t.addGrade(new Grade(new Subject("Prog", GradeType.EXAM, 2), GradeScore.GOOD, true));
        assertTrue(student.canTransferToBudget(), "Должен перевестись с тройкой по зачету");
        t.addGrade(new Grade(new Subject("Phil", GradeType.EXAM, 2), GradeScore.SATISFACTORY, true));
        assertFalse(student.canTransferToBudget(), "Не должен переводиться с тройкой по экзамену");
    }

    @Test
    void testRedDiploma() {
        Student student = new Student.Builder("Ivan", "Ivanov", "IT", StudyForm.BUDGET)
                .qualifyingWorkGrade(5)
                .build();
        Table t = student.getTranscript();
        t.setCurrentSemester(8);
        t.addGrade(new Grade(new Subject("S1", GradeType.EXAM, 1), GradeScore.EXCELLENT, true));
        t.addGrade(new Grade(new Subject("S2", GradeType.EXAM, 2), GradeScore.EXCELLENT, true));
        assertTrue(student.canGetRedDiploma());
        t.addGrade(new Grade(new Subject("S3", GradeType.EXAM, 3), GradeScore.SATISFACTORY, true));
        assertFalse(student.canGetRedDiploma());
    }
}
