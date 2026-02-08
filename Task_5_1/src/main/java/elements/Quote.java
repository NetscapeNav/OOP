package elements;

import java.util.Objects;

public class Quote extends Element {
    private final Element text;

    public Quote(Element text) {
        this.text = text;
    }

    public Quote(String text) {
        this.text = new Text(text);
    }

    @Override
    public String serialize() {
        return "> " + text.serialize();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Quote quote = (Quote) o;
        return Objects.equals(text, quote.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }
}
