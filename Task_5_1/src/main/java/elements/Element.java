package elements;

public abstract class Element {
    public abstract String serialize();

    @Override
    public String toString() {
        return serialize();
    }
}
