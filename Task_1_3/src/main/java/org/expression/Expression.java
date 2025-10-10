package org.expression;

import org.example.exception.EvaluationException;

import java.util.HashMap;
import java.util.Map;

public abstract class Expression {
    public abstract int method() throws EvaluationException;
    public abstract String print();
    public abstract Expression derivative(String varName);

    public final int eval(String equations) throws EvaluationException {
        Map<String, Integer> context = parseValuesToMap(equations);
        return eval(context);
    }

    public abstract int eval(Map<String, Integer> context) throws EvaluationException;

    private static Map<String, Integer> parseValuesToMap(String equations) {
        Map<String, Integer> context = new HashMap<>();
        if (equations == null || equations.trim().isEmpty()) {
            return context;
        }
        String[] parts = equations.split(";");
        for (String part : parts) {
            String[] assignment = part.split("=");
            if (assignment.length == 2) {
                context.put(assignment[0].trim(), Integer.parseInt(assignment[1].trim()));
            }
        }
        return context;
    }
}

