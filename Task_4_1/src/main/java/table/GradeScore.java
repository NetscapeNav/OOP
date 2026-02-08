package table;

public enum GradeScore {
    UNSATISFACTORY(2, "Неудовлетворительно"),
    SATISFACTORY(3, "Удовлетворительно"),
    GOOD(4, "Хорошо"),
    EXCELLENT(5, "Отлично");

    private final int value;
    private final String gradeName;

    GradeScore(int value, String gradeName) {
        this.value = value;
        this.gradeName = gradeName;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return gradeName;
    }
}
