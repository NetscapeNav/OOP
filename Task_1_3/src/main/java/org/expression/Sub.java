package org.expression;

import org.example.exception.EvaluationException;

public class Sub extends Expression {
    private Expression add1;
    private Expression add2;

    public Sub(Expression x, Expression y) {
        add1 = x;
        add2 = y;
    }

    @Override
    public int method() throws EvaluationException {
        return add1.method() - add2.method();
    }

    @Override
    public String print() {
        return String.format("(%s - %s)", add1.print(), add2.print());
    }

    @Override
    public Expression derivative(String varName) {
        return new Sub(add1.derivative(varName), add2.derivative(varName));
    }

    @Override
    public int eval(String equation) throws EvaluationException {
        int leftValue = add1.eval(equation);
        int rightValue = add2.eval(equation);
        return leftValue - rightValue;
    }
}
