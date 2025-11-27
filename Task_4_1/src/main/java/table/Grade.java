package table;

import java.util.Date;

public class Grade {
    private final GradeScore score;
    private final Date date;
    private final Subject subject;
    private final boolean isFinal;

    public Grade(Subject subject, GradeScore score, boolean isFinal) {
        if (subject == null) throw new IllegalArgumentException("Некорректный предмет");
        if (score == null) throw new IllegalArgumentException("Некорректная оценка");

        this.subject = subject;
        this.score = score;
        this.isFinal = isFinal;
        this.date = new Date();
    }

    public Grade(Grade other) {
        this.subject = other.subject;
        this.score = other.score;
        this.isFinal = other.isFinal;
        this.date = new Date(other.date.getTime());
    }

    public GradeScore getScore() { return score; }

    public int getIntScore() { return score.getValue(); }

    public Date getDate() { return new Date(date.getTime()); }

    public Subject getSubject() { return subject; }

    public boolean isFinal() { return isFinal; }

    @Override
    public String toString() {
        return String.format("%s (%s) - %s",
                subject.getName(),
                subject.getType(),
                score
        );
    }
}
