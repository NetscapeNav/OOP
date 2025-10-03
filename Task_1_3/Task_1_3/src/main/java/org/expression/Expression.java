package org.expression;

public abstract class Expression {
    public abstract int method();
    public abstract void print();
    public abstract Expression derivative(String varName);
    public abstract int eval(String equation);
}

