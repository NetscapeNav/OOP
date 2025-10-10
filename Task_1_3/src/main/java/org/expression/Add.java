package org.expression;

import org.example.exception.EvaluationException;

import java.util.Map;

public class Add extends Expression {
    private Expression add1;
    private Expression add2;

    public Add(Expression x, Expression y) {
        add1 = x;
        add2 = y;
    }

    @Override
    public int method() throws EvaluationException {
        return add1.method() + add2.method();
    }

    @Override
    public String print() {
        return String.format("(%s + %s)", add1.print(), add2.print());
    }

    @Override
    public Expression derivative(String varName) {
        return new Add(add1.derivative(varName), add2.derivative(varName));
    }

    @Override
    public int eval(Map<String, Integer> context) throws EvaluationException {
        int leftValue = add1.eval(context);
        int rightValue = add2.eval(context);
        return leftValue + rightValue;
    }
}
