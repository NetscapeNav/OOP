package elements;

import java.util.Objects;

public class Task extends Element {
    private Element text;
    private boolean completed;

    public Task(Element text, boolean completed) {
        this.text = text;
        this.completed = completed;
    }

    public Task(String text, boolean completed) {
        this(new Text(text), completed);
    }

    @Override
    public String serialize() {
        return (completed ? "- [x] " : "- [ ] ") + text.serialize();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return completed == task.completed && Objects.equals(text, task.text);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, completed);
    }
}
