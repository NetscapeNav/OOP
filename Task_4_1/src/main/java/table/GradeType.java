package table;

public enum GradeType {
    EXAM("Экзамен"),
    DIFF_CREDIT("Дифференцированный зачёт"),
    CREDIT("Зачёт");

    private final String russianType;

    GradeType(String russianType) {
        this.russianType = russianType;
    }

    @Override
    public String toString() {
        return russianType;
    }
}
