package table;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.regex.Pattern;

@Getter
@Builder
public class Student {
    private static final String NAME_PATTERN = "^[a-zA-Zа-яА-ЯёЁ\\s-]+$";

    private String firstName;
    private String lastName;

    @lombok.Builder.Default
    private String middleName = "";

    private String speciality;

    @Setter
    private StudyForm studyForm;

    @lombok.Builder.Default
    private int qualifyingWorkGrade = 0;

    @Setter
    @lombok.Builder.Default
    private Table transcript = new Table();

    public Student(String firstName, String lastName, String middleName, String speciality, StudyForm studyForm, int qualifyingWorkGrade, Table transcript) {
        setFirstName(firstName);
        setLastName(lastName);
        setMiddleName(middleName);
        setSpeciality(speciality);

        if (studyForm == null) {
            throw new IllegalArgumentException("Форма обучения не может быть null");
        }
        this.studyForm = studyForm;

        setTranscript(transcript != null ? transcript : new Table());
        setQualifyingWorkGrade(qualifyingWorkGrade);
    }

    public void setFirstName(String firstName) {
        validateName(firstName, "Имя");
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        validateName(lastName, "Фамилия");
        this.lastName = lastName;
    }

    public void setMiddleName(String middleName) {
        if (middleName != null && !middleName.trim().isEmpty()) {
            validateName(middleName, "Отчество");
        }
        this.middleName = middleName != null ? middleName : "";
    }

    public void setSpeciality(String speciality) {
        validateName(speciality, "Специальность");
        this.speciality = speciality;
    }

    public void setQualifyingWorkGrade(int qualifyingWorkGrade) {
        if (qualifyingWorkGrade != 0 && (qualifyingWorkGrade < 2 || qualifyingWorkGrade > 5)) {
            throw new IllegalArgumentException("Оценка за диплом должна быть от 2 до 5");
        }
        this.qualifyingWorkGrade = qualifyingWorkGrade;
    }

    private void validateName(String name, String fieldName) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " не может быть пустым");
        }
        if (!Pattern.matches(NAME_PATTERN, name)) {
            throw new IllegalArgumentException(fieldName + " содержит недопустимые символы");
        }
    }

    public float averageScore() {
        if (transcript == null || transcript.getGrades().isEmpty()) return 0.0f;

        return (float) transcript.getGrades().stream()
                .mapToInt(Grade::getIntScore)
                .average()
                .orElse(0.0);
    }

    public boolean canTransferToBudget() {
        if (this.studyForm == StudyForm.BUDGET) return false;
        if (transcript == null) return false;

        List<Grade> lastTwoSessions = transcript.getLastTwoExamSessions();
        if (lastTwoSessions.isEmpty()) return false;

        return lastTwoSessions.stream().allMatch(g -> {
            int val = g.getIntScore();
            boolean isExam = g.getSubject().getType() == GradeType.EXAM;
            return val > 3 || (val == 3 && !isExam);
        });
    }

    public boolean canGetRedDiploma() {
        if (qualifyingWorkGrade != 5) return false;
        if (transcript == null) return false;

        List<Grade> finalGrades = transcript.getFinalGrades();
        if (finalGrades.isEmpty()) return false;

        boolean noTroikas = finalGrades.stream().noneMatch(g -> g.getIntScore() < 4);
        long fiveCount = finalGrades.stream().filter(g -> g.getIntScore() == 5).count();

        return noTroikas && ((float) fiveCount / finalGrades.size() >= 0.75);
    }

    public boolean canGetScholarship() {
        if (transcript == null) return false;

        List<Grade> currentGrades = transcript.getCurrentSemesterGrades();
        return !currentGrades.isEmpty() &&
                currentGrades.stream().allMatch(g -> g.getIntScore() >= 4);
    }

    public boolean canGetIncreasedScholarship() {
        if (transcript == null) return false;

        List<Grade> currentGrades = transcript.getCurrentSemesterGrades();
        return !currentGrades.isEmpty() &&
                currentGrades.stream().allMatch(g -> g.getIntScore() == 5);
    }

    public String getFullReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n================================================\n");
        sb.append("         ОТЧЕТ ОБ УСПЕВАЕМОСТИ СТУДЕНТА         \n");
        sb.append("================================================\n");

        sb.append(String.format("Студент:       %s %s %s\n", lastName, firstName, middleName));
        sb.append(String.format("Специальность: %s\n", speciality));
        sb.append(String.format("Форма:         %s\n", studyForm == StudyForm.BUDGET ? "Бюджет" : "Платное"));
        sb.append("------------------------------------------------\n");

        sb.append(transcript.toString());

        sb.append("------------------------------------------------\n");
        sb.append("               АНАЛИТИКА И ПРОГНОЗЫ             \n");
        sb.append("------------------------------------------------\n");

        sb.append(String.format("Текущий средний балл: %.2f\n", averageScore()));

        boolean scholarship = canGetScholarship();
        boolean increasedScholarship = canGetIncreasedScholarship();

        sb.append("Академическая стипендия: ").append(scholarship ? "ДА" : "НЕТ").append("\n");
        if (scholarship) {
            sb.append("Повышенная стипендия: ").append(increasedScholarship ? "ДА" : "НЕТ").append("\n");
        }

        if (studyForm == StudyForm.PAID) {
            sb.append("Перевод на бюджет: ").append(canTransferToBudget() ? "ВОЗМОЖЕН" : "НЕТ").append("\n");
        } else {
            sb.append("Перевод на бюджет: (уже на бюджете)\n");
        }

        sb.append("Красный диплом: ").append(canGetRedDiploma() ? "ВОЗМОЖЕН" : "НЕТ").append("\n");
        sb.append("================================================\n");

        return sb.toString();
    }
}
