package org.example;

import org.example.exception.EvaluationException;
import org.example.exception.ParsingException;
import org.expression.*;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ExpressionParser parser = new ExpressionParser();
        Scanner scanner = new Scanner(System.in);
        try {
            Expression e = parser.parse("(x + (x * 2))");
            System.out.println(e.print());
            Expression de = e.derivative("x");
            System.out.println(de.print());
            int result = e.eval("x = 2; y = 13");
            System.out.println(result);
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
        try {
            System.out.print("Enter an expression: ");
            String input = scanner.nextLine();
            Expression e = parser.parse(input);
            System.out.println("Success.");
            System.out.println("Your expression: " + e.print());
            Expression de = e.derivative("x");
            System.out.println("Derivative about 'x': " + de.print());
            System.out.print("Enter value of variables (e.g. x = 10; y = 13): ");
            String values = scanner.nextLine();
            int result = e.eval(values);
            System.out.println("Result of calculation: " + result);
        } catch (ParsingException | EvaluationException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}