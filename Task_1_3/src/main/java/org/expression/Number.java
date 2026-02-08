package org.expression;

import org.example.exception.EvaluationException;

import java.util.Map;

public class Number extends Expression {
    private final int value;

    public Number(int x) {
        value = x;
    }

    @Override
    public Expression simplify() { return this; }

    @Override
    public int evalWithOnlyNumbers() {
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
    public int eval(Map<String, Integer> context) throws EvaluationException {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Number number = (Number) obj;
        return value == number.value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(value);
    }
}
