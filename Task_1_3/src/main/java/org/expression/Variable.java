package org.expression;

import org.example.exception.EvaluationException;

public class Variable extends Expression {
    private int value;
    private String name;

    public Variable(String name) {
        this.name = name;
    }

    @Override
    public int method() {
        return value;
    }

    @Override
    public String print() {
        return name;
    }

    @Override
    public Expression derivative(String varName) {
        if (name.equals(varName)) {
            return new Number(1);
        } else {
            return new Number(0);
        }
    }

    @Override
    public int eval(String equations) throws EvaluationException {
        String[] parts = equations.split(";");
        for (String part : parts) {
            part = part.trim();
            if (part.startsWith(name + " = ")) {
                String valueStr = part.substring(name.length() + 3).trim();
                return Integer.parseInt(valueStr);
            }
        }
        throw new EvaluationException("No value assigned for variable: " + name);
    }
}
