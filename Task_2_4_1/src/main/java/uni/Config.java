package uni;

import groovy.lang.Closure;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Config {
    private List<Group> groups = new ArrayList<>();
    private List<Task> tasks = new ArrayList<>();
    private List<Convertion> conversions = new ArrayList<>();
    private Map<Integer, List<String>> groupAssignments = new HashMap<>();

    public Config() {
    }

    public void include(String fileName) {
        try {
            org.codehaus.groovy.control.CompilerConfiguration cc = new org.codehaus.groovy.control.CompilerConfiguration();
            cc.setScriptBaseClass("groovy.util.DelegatingScript");

            groovy.lang.GroovyShell shell = new groovy.lang.GroovyShell(this.getClass().getClassLoader(), new groovy.lang.Binding(), cc);

            groovy.util.DelegatingScript script = (groovy.util.DelegatingScript) shell.parse(new java.io.File(fileName));
            script.setDelegate(this);
            script.run();
        } catch (Exception e) {
            System.out.println("Ошибка при импорте файла: " + fileName);
            e.printStackTrace();
        }
    }

    public void tasks(Closure cl) {
        cl.setDelegate(this);
        cl.setResolveStrategy(Closure.DELEGATE_FIRST);
        cl.call();
    }

    public void task(String id, Closure cl) {
        Task newTask = new Task();
        newTask.setId(id);

        cl.setDelegate(newTask);
        cl.setResolveStrategy(Closure.DELEGATE_FIRST);
        cl.call();

        tasks.add(newTask);
    }

    public void groups(Closure cl) {
        cl.setDelegate(this);
        cl.call();
    }

    public void group(int number, Closure cl) {
        Group newGroup = new Group();
        newGroup.setNumber(number);

        cl.setDelegate(newGroup);
        cl.setResolveStrategy(Closure.DELEGATE_FIRST);
        cl.call();

        groups.add(newGroup);
    }

    public void assignments(Closure cl) {
        cl.setDelegate(this);
        cl.setResolveStrategy(Closure.DELEGATE_FIRST);
        cl.call();
    }

    public AssignmentBuilder assign(String taskId) {
        return new AssignmentBuilder(taskId);
    }

    public class AssignmentBuilder {
        private String taskId;

        public AssignmentBuilder(String taskId) {
            this.taskId = taskId;
        }

        public void to(int targetGroup) {
            System.out.println("-> Назначение: задача " + taskId + " для группы " + targetGroup);
            groupAssignments.putIfAbsent(targetGroup, new ArrayList<>());
            groupAssignments.get(targetGroup).add(taskId);
        }
    }
    
    public List<String> getAssignedTasksForGroup(int groupId) {
        return groupAssignments.getOrDefault(groupId, new ArrayList<>());
    }

    public void checkpoints(Closure cl) {
        cl.setDelegate(this);
        cl.setResolveStrategy(Closure.DELEGATE_FIRST);
        cl.call();
    }

    public void point(String name, String date) {
        System.out.println("-> Добавлена контрольная точка: " + name + " (дата: " + date + ")");
    }

    public void settings(Closure cl) {
        cl.setDelegate(this);
        cl.setResolveStrategy(Closure.DELEGATE_FIRST);
        cl.call();
    }

    public void setTimeout(int timeout) {
        System.out.println("-> Установлен таймаут: " + timeout);
    }

    public void setStyleGuide(String styleGuide) {
        System.out.println("-> Установлен стиль: " + styleGuide);
    }
    
    public void convertion(int score, int mark) {
        conversions.add(new Convertion(score, mark));
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public List<Group> getGroups() {
        return groups;
    }
    
    public List<Convertion> getConversions() {
        return conversions;
    }
}
