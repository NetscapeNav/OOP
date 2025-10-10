package org.expression;

import org.example.exception.EvaluationException;

public abstract class Expression {
    public abstract int method() throws EvaluationException;
    public abstract String print();
    public abstract Expression derivative(String varName);
    public abstract int eval(String equation) throws EvaluationException;
}

