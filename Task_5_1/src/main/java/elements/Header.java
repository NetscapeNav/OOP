package elements;

import java.util.Objects;

public class Header extends Element {
    private final int level;
    private final Element text;

    public Header(int level, Element text) {
        if (level < 1 || level > 6) throw new IllegalArgumentException("Level must be 1-6");
        this.level = level;
        this.text = text;
    }

    public Header(int level, String header) {
        this(level, new Text(header));
    }

    @Override
    public String serialize() {
        return "#".repeat(level) + " " + text.serialize();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Header header1 = (Header) o;
        return level == header1.level && Objects.equals(text, header1.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(level, text);
    }
}
