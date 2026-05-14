package uni;

public class Student {
    private int id;
    private String name;
    private String githubNick;
    private String repoURL;

    public Student() {
    }

    public Student(int id, String name, String githubNick, String repoURL) {
        this.id = id;
        this.name = name;
        this.githubNick = githubNick;
        this.repoURL = repoURL;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGithubNick() { return githubNick; }
    public void setGithubNick(String githubNick) { this.githubNick = githubNick; }

    public String getRepoURL() { return repoURL; }
    public void setRepoURL(String repoURL) { this.repoURL = repoURL; }
}
