package org.expression;

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
    public void print() {
        System.out.print(value);
    }

    @Override
    public Expression derivative(String varName) {
        return new Number(0);
    }

    @Override
    public int eval(String equation) {
        return value;
    }
}
