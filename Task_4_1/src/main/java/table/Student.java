package table;

import java.util.List;
import java.util.regex.Pattern;

public class Student {
    private static final String NAME_PATTERN = "^[a-zA-Zа-яА-ЯёЁ\\s-]+$";

    private String firstName;
    private String lastName;
    private String middleName;
    private String speciality;
    private StudyForm studyForm;
    private int qualifyingWorkGrade;
    private Table transcript;

    private Student(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.speciality = builder.speciality;
        this.studyForm = builder.studyForm;

        this.middleName = builder.middleName;
        this.qualifyingWorkGrade = builder.qualifyingWorkGrade;
        this.transcript = builder.transcript != null ? new Table(builder.transcript) : new Table();
    }

    public static class Builder {
        private final String firstName;
        private final String lastName;
        private final String speciality;
        private final StudyForm studyForm;

        private String middleName = "";
        private int qualifyingWorkGrade = 0;
        private Table transcript;

        public Builder(String firstName, String lastName, String speciality, StudyForm studyForm) {
            validateName(firstName);
            validateName(lastName);
            validateName(speciality);

            if (studyForm == null) throw new IllegalArgumentException("Форма обучения не может быть null");

            this.firstName = firstName;
            this.lastName = lastName;
            this.speciality = speciality;
            this.studyForm = studyForm;
            this.transcript = new Table();
        }

        public Builder middleName(String middleName) {
            if (middleName != null && !middleName.trim().isEmpty()) {
                validateName(middleName);
            }
            this.middleName = middleName != null ? middleName : "";
            return this;
        }

        public Builder qualifyingWorkGrade(int grade) {
            this.qualifyingWorkGrade = grade;
            return this;
        }

        public Builder transcript(Table transcript) {
            this.transcript = transcript;
            return this;
        }

        public Builder currentSemester(int semester) {
            this.transcript.setCurrentSemester(semester);
            return this;
        }

        private void validateName(String name) {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Поля не могут быть пустыми");
            }
            if (!Pattern.matches(NAME_PATTERN, name)) {
                throw new IllegalArgumentException("Некоторые поля содержит недопустимые символы (разрешены только буквы, пробел и дефис)");
            }
        }

        public Student build() {
            if (qualifyingWorkGrade != 0 && (qualifyingWorkGrade < 2 || qualifyingWorkGrade > 5)) {
                throw new IllegalArgumentException("Оценка за диплом должна быть от 2 до 5");
            }
            return new Student(this);
        }
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getMiddleName() { return middleName; }
    public String getSpeciality() { return speciality; }
    public StudyForm getStudyForm() { return studyForm; }
    public int getQualifyingWorkGrade() { return qualifyingWorkGrade; }
    public Table getTranscript() { return transcript; }

    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Фамилия не может быть пустой");
        }
        this.lastName = lastName;
    }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public void setSpeciality(String speciality) { this.speciality = speciality; }
    public void setStudyForm(StudyForm studyForm) {
        if (studyForm == null) {
            throw new IllegalArgumentException("Форма обучения не может быть null");
        }
        this.studyForm = studyForm;
    }
    public void setQualifyingWorkGrade(int grade) {
        if (grade != 0 && (grade < 2 || grade > 5)) {
            throw new IllegalArgumentException("Оценка за диплом должна быть от 2 до 5");
        }
        this.qualifyingWorkGrade = grade;
    }
    public void setTranscript(Table transcript) { this.transcript = transcript; }

    public float averageScore() {
        List<Grade> allGrades = transcript.getGrades();
        if (allGrades.isEmpty()) return 0.0f;

        long sum = 0;
        for (Grade grade : allGrades) {
            sum += grade.getIntScore();
        }

        return (float) sum / allGrades.size();
    }

    public boolean canTransferToBudget() {
        if (this.studyForm == StudyForm.BUDGET) return false;
        List<Grade> lastTwoSessions = transcript.getLastTwoExamSessions();
        if (lastTwoSessions.isEmpty()) return false;
        for (Grade grade : lastTwoSessions) {
            int val = grade.getIntScore();
            Subject subject = grade.getSubject();
            if (val < 3) return false;
            if (val == 3 && subject.getType() == GradeType.EXAM) {
                return false;
            }
        }
        return true;
    }

    public boolean canGetRedDiploma() {
        if (qualifyingWorkGrade != 5) {
            return false;
        }
        List<Grade> finalGrades = transcript.getFinalGrades();
        if (finalGrades.isEmpty()) { return false; }
        int fiveCount = 0;
        for (Grade grade : finalGrades) {
            if (grade.getIntScore() < 4) return false;
            if (grade.getIntScore() == 5) fiveCount++;
        }
        return (float) fiveCount / (finalGrades.size()) >= 0.75;
    }

    public boolean canGetScholarship() {
        if (transcript == null) return false;
        List<Grade> currentGrades = transcript.getCurrentSemesterGrades();
        if (currentGrades.isEmpty()) return false;
        for (Grade grade : currentGrades) {
            if (grade.getIntScore() < 4) return false;
        }
        return true;
    }

    public boolean canGetIncreasedScholarship() {
        if (transcript == null || !canGetScholarship()) return false;
        List<Grade> currentGrades = transcript.getCurrentSemesterGrades();
        for (Grade grade : currentGrades) {
            if (grade.getIntScore() < 5) return false;
        }
        return true;
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
