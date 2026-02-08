package elements;

import java.util.Objects;

public class Link extends Element {
    private final Element text;
    private final String url;

    public Link(Element text, String url) {
        this.text = text;
        this.url = url;
    }

    public Link(String text, String url) {
        this(new Text(text), url);
    }

    @Override
    public String serialize() {
        return "[" + text.serialize() + "](" + url + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return Objects.equals(text, link.text) && Objects.equals(url, link.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, url);
    }
}
