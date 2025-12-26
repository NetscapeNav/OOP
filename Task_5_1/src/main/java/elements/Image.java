package elements;

import java.util.Objects;

public class Image extends Element {
    private final String text;
    private final String url;

    public Image(String text, String url) {
        this.text = text;
        this.url = url;
    }

    @Override
    public String serialize() {
        return "![" + text + "](" + url + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Image image = (Image) o;
        return Objects.equals(text, image.text) && Objects.equals(url, image.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, url);
    }
}
