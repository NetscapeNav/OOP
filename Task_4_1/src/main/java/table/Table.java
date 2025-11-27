package table;

import java.util.*;
import java.util.stream.Collectors;

public class Table {
    private List<Grade> grades = new ArrayList<>();
    private int currentSemester;

    public Table() {}

    public Table(Table other) {
        this.currentSemester = other.currentSemester;
        this.grades = new ArrayList<>();
        for (Grade grade : other.grades) {
            this.grades.add(new Grade(grade));
        }
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
    public int getCurrentSemester() { return currentSemester; }

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
        if (grades == null || grades.isEmpty()) {
            return new ArrayList<>();
        }

        Set<Integer> examSemester = new HashSet<>();
        for (Grade grade : grades) {
            Subject subject = grade.getSubject();
            if (subject.getType() == GradeType.EXAM) {
                examSemester.add(subject.getSemester());
            }
        }

        List<Integer> sortedSemester = new ArrayList<>(examSemester);
        sortedSemester.sort(Collections.reverseOrder());

        List<Integer> targetSemester = new ArrayList<>();
        for (Integer sem : sortedSemester) {
            if (sem < currentSemester) {
                targetSemester.add(sem);
                if (targetSemester.size() == 2) break;
            }
        }

        List<Grade> result = new ArrayList<>();
        for (Grade grade : grades) {
            Subject subject = grade.getSubject();
            if (targetSemester.contains(subject.getSemester())) {
                result.add(grade);
            }
        }

        return Collections.unmodifiableList(result);
    }

    public List<Grade> getFinalGrades() {
        if (grades == null) {
            return new ArrayList<>();
        }
        List<Grade> result = new ArrayList<>();
        for (Grade grade : grades) {
            if (grade.isFinal()) result.add(grade);
        }

        return result;
    }

    public List<Grade> getCurrentSemesterGrades() {
        if (grades == null) {
            return new ArrayList<>();
        }
        List<Grade> result = new ArrayList<>();
        for (Grade grade : grades) {
            Subject subject = grade.getSubject();
            if (subject.getSemester() == currentSemester) {
                result.add(grade);
            }
        }

        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Зачетная книжка (Текущий семестр: ").append(currentSemester).append(") ===\n");

        if (grades.isEmpty()) {
            sb.append(" (Зачетка пуста)\n");
        } else {
            grades.sort(Comparator.comparingInt((Grade g) -> g.getSubject().getSemester()));

            for (Grade grade : grades) {
                sb.append(" Семестр ").append(grade.getSubject().getSemester())
                        .append(": ").append(grade.toString()).append("\n");
            }
        }
        return sb.toString();
    }
}
