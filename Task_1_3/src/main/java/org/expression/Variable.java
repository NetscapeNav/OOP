package org.expression;

import org.example.exception.EvaluationException;

import java.util.Map;

public class Variable extends Expression {
    private int value;
    private final String name;

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
    public int eval(Map<String, Integer> context) throws EvaluationException {
        Integer value = context.get(name);
        if (value == null) {
            throw new EvaluationException("No value assigned for variable: " + name);
        }
        return value;
    }
}
