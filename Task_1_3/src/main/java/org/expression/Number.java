package org.expression;

import org.example.exception.EvaluationException;

public class Number extends Expression {
    private int value;

    public Number(int x) {
        value = x;
    }

    @Override
    public int method() {
        return value;
    }

    @Override
    public String print() {
        return String.valueOf(value);
    }

    @Override
    public Expression derivative(String varName) {
        return new Number(0);
    }

    @Override
    public int eval(String equation) throws EvaluationException {
        return value;
    }
}
