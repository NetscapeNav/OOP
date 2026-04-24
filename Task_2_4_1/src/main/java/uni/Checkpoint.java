package uni;

import java.time.LocalDate;

public class Checkpoint {
    private final String name;
    private final LocalDate date;

    public Checkpoint(String name, String date) {
        this.name = name;
        this.date = LocalDate.parse(date);
    }

    public String getName() { return name; }
    public LocalDate getDate() { return date; }
}
