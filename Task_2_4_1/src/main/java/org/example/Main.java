package org.example;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import org.codehaus.groovy.control.CompilerConfiguration;
import uni.Checker;
import uni.Config;
import uni.Group;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        Config rootConfig = new Config();

        CompilerConfiguration cc = new CompilerConfiguration();
        cc.setScriptBaseClass("groovy.util.DelegatingScript");

        Binding binding = new Binding();
        GroovyShell shell = new GroovyShell(Main.class.getClassLoader(), binding, cc);

        try {
            DelegatingScript script = (DelegatingScript) shell.parse(new File("config.groovy"));
            script.setDelegate(rootConfig);
            script.run();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Загружено задач: " + rootConfig.getTasks().size());

        for (Group g : rootConfig.getGroups()) {
            System.out.println("Группа " + g.getNumber() + ", студентов: " + g.getStudents().size());
        }

        if (args.length > 0 && args[0].equals("test")) {
            Checker checker = new Checker(rootConfig);
            checker.check();
        } else {
            System.out.println("\nКоманда не распознана или не введена.");
            System.out.println("Для запуска проверок используйте аргумент 'test' (например: java -jar app.jar test)");
        }
    }
}
