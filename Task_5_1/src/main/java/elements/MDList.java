package elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MDList extends Element {
    private final List<Element> items = new ArrayList<>();
    private final boolean ordered;

    public MDList(boolean ordered, String... items) {
        this.ordered = ordered;
        for (String item : items) {
            this.items.add(new Text(item));
        }
    }

    public MDList(boolean ordered, Element... items) {
        this.ordered = ordered;
        this.items.addAll(Arrays.asList(items));
    }

    @Override
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < items.size(); i++) {
            if (ordered) {
                sb.append(i+1).append(". ").append(items.get(i).serialize()).append("\n");
            } else {
                sb.append("- ").append(items.get(i).serialize()).append("\n");
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MDList mdList = (MDList) o;
        return ordered == mdList.ordered && Objects.equals(items, mdList.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, ordered);
    }
}
