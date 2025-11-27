package table;

import java.util.regex.Pattern;

public class Subject {
    private String name;
    private GradeType type;
    private int semester;

    private static final String SUBJECT_PATTERN = "^[a-zA-Zа-яА-ЯёЁ0-9\\s\\-.,]+$";

    public Subject(String name, GradeType type, int semester) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Некорректное название предмета");
        }
        if (!Pattern.matches(SUBJECT_PATTERN, name)) {
            throw new IllegalArgumentException("Название предмета содержит недопустимые символы");
        }
        if (type == null) {
            throw new IllegalArgumentException("Некорректный тип оценки предмета");
        }
        if (semester <= 0) {
            throw new IllegalArgumentException("Некорректный номер семестра");
        }

        this.name = name;
        this.type = type;
        this.semester = semester;
    }

    public String getName() { return name; }

    public GradeType getType() {
        return type;
    }

    public int getSemester() {
        return semester;
    }

}
