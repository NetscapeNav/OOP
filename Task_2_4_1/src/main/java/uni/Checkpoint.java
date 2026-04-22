package uni;

import java.util.Date;

public class Checkpoint {
    private final int id;
    private final String name;
    private final Date date;

    public Checkpoint(int id, String name, Date date) {
        this.id = id;
        this.name = name;
        this.date = date;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public Date getDate() { return date; }
}
