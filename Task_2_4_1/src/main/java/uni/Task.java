package uni;

import java.util.Date;

public class Task {
    private String id;
    private String name;
    //private Date softDeadline;
    //private Date hardDeadline;
    private String softDeadline;
    private String hardDeadline;

    private int maxScore;

    public Task() {
    }

    public Task(String id, String name, String softDeadline, String hardDeadline, int maxScore) {
        this.id = id;
        this.name = name;
        this.softDeadline = softDeadline;
        this.hardDeadline = hardDeadline;
        this.maxScore = maxScore;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSoftDeadline() { return softDeadline; }
    public void setSoftDeadline(String softDeadline) { this.softDeadline = softDeadline; }

    public String getHardDeadline() { return hardDeadline; }
    public void setHardDeadline(String hardDeadline) { this.hardDeadline = hardDeadline; }

    public int getMaxScore() { return maxScore; }
    public void setMaxScore(int maxScore) { this.maxScore = maxScore; }
}
