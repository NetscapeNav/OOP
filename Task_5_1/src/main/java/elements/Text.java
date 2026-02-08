package elements;

import java.util.Objects;

public class Text extends Element {
    private final String text;

    public Text(String text) {
        this.text = text;
    }

    @Override
    public String serialize() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Text text1 = (Text) o;
        return Objects.equals(text, text1.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text);
    }

    public abstract static class FormattedText extends Element {
        protected Element text;

        public FormattedText(Element text) {
            this.text = text;
        }

        public FormattedText(String text) {
            this.text = new Text(text);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            FormattedText that = (FormattedText) o;
            return Objects.equals(text, that.text);
        }

        @Override
        public int hashCode() {
            return Objects.hash(text);
        }
    }

    public static class Bold extends FormattedText {
        public Bold(Element content) { super(content); }
        public Bold(String text) { super(text); }

        @Override
        public String serialize() {
            return "**" + text.serialize() + "**";
        }
    }

    public static class Italic extends FormattedText {
        public Italic(Element content) { super(content); }
        public Italic(String text) { super(text); }

        @Override
        public String serialize() {
            return "*" + text.serialize() + "*";
        }
    }

    public static class Slashed extends FormattedText {
        public Slashed(Element content) { super(content); }
        public Slashed(String text) { super(text); }

        @Override
        public String serialize() {
            return "~~" + text.serialize() + "~~";
        }
    }

    public static class Code extends FormattedText {
        public Code(Element content) { super(content); }
        public Code(String text) { super(text); }

        @Override
        public String serialize() {
            return "`" + text.serialize() + "`";
        }
    }

    public static class CodeBlock extends FormattedText {
        public CodeBlock(Element content) { super(content); }
        public CodeBlock(String text) { super(text); }

        @Override
        public String serialize() {
            return "```\n" + text.serialize() + "\n```";
        }
    }
}
