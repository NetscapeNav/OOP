package uni;

import groovy.lang.Closure;
import java.util.ArrayList;
import java.util.List;

public class Group {
    private int number;
    private List<Student> students = new ArrayList<>();

    public Group() {
    }

    public Group(int number, List<Student> students) {
        this.number = number;
        this.students = students;
    }

    public void student(String nick, Closure cl) {
        Student student = new Student();
        student.setGithubNick(nick);

        cl.setDelegate(student);
        cl.setResolveStrategy(Closure.DELEGATE_FIRST);
        cl.call();

        students.add(student);
    }

    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }

    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }
}
