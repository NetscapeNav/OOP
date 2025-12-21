package table;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
public class Table {

    private List<Grade> grades = new ArrayList<>();
    @Getter
    private int currentSemester;

    public Table(Table other) {
        this.currentSemester = other.currentSemester;
        this.grades = other.grades.stream()
                .map(Grade::new)
                .collect(Collectors.toList());
    }

    public void addGrade(Grade grade) {
        if (grade == null) {
            throw new IllegalArgumentException("Нельзя добавить пустую оценку");
        }
        if (currentSemester > 0 && grade.getSubject().getSemester() > currentSemester) {
            throw new IllegalArgumentException(String.format("Нельзя добавить оценку за %d семестр, так как текущий семестр %d",
                    grade.getSubject().getSemester(), currentSemester));
        }
        this.grades.add(grade);
    }

    public List<Grade> getGrades() { return Collections.unmodifiableList(new ArrayList<>(grades)); }

    public void setGrades(List<Grade> grades) {
        this.grades = grades != null ? grades : new ArrayList<>();
    }

    public void setCurrentSemester(int currentSemester) {
        if (currentSemester <= 0) {
            throw new IllegalArgumentException("Некорректный номер семестра");
        }
        this.currentSemester = currentSemester;
    }

    public List<Grade> getLastTwoExamSessions() {
        List<Integer> targetSemesters = grades.stream()
                .filter(g -> g.getSubject().getType() == GradeType.EXAM)
                .map(g -> g.getSubject().getSemester())
                .distinct()
                .sorted(Comparator.reverseOrder())
                .filter(sem -> sem < currentSemester)
                .limit(2)
                .collect(Collectors.toList());

        return grades.stream()
                .filter(g -> targetSemesters.contains(g.getSubject().getSemester()))
                .collect(Collectors.toUnmodifiableList());
    }

    public List<Grade> getFinalGrades() {
        return grades.stream()
                .filter(Grade::isFinal)
                .collect(Collectors.toList());
    }

    public List<Grade> getCurrentSemesterGrades() {
        return grades.stream()
                .filter(g -> g.getSubject().getSemester() == currentSemester)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Зачетная книжка (Текущий семестр: ").append(currentSemester).append(") ===\n");

        if (grades.isEmpty()) {
            sb.append(" (Зачетка пуста)\n");
        } else {
            String gradesStr = grades.stream()
                    .sorted(Comparator.comparingInt(g -> g.getSubject().getSemester()))
                    .map(g -> " Семестр " + g.getSubject().getSemester() + ": " + g.toString())
                    .collect(Collectors.joining("\n"));
            sb.append(gradesStr).append("\n");
        }
        return sb.toString();
    }
}
