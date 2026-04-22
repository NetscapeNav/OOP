package org.example;

import check.Checker;
import check.Logger;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.util.DelegatingScript;
import org.codehaus.groovy.control.CompilerConfiguration;
import uni.*;

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

        Logger.info("Загружено задач: " + rootConfig.getTasks().size());

        for (Group g : rootConfig.getGroups()) {
            Logger.info("Группа " + g.getNumber() + ", студентов: " + g.getStudents().size());
        }

        if (args.length > 0 && args[0].equals("test")) {
            Checker checker = new Checker(rootConfig);
            checker.check();
        } else {
            Logger.info("\nКоманда не распознана или не введена.");
            Logger.info("Для запуска проверок используйте аргумент 'test' (например: java -jar app.jar test)");
        }
    }
}
