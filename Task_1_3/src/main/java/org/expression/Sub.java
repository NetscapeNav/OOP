package org.expression;

import org.example.exception.EvaluationException;

import java.util.Map;

public class Sub extends Expression {
    private final Expression add1;
    private final Expression add2;

    public Sub(Expression x, Expression y) {
        add1 = x;
        add2 = y;
    }

    private boolean isNumber(Expression exp, int value) {
        if (exp instanceof Number) {
            try {
                return exp.evalWithOnlyNumbers() == value;
            } catch (EvaluationException e) {
                return false;
            }
        }
        return false;
    }

    @Override
    public Expression simplify() {
        Expression simpLeft = add1.simplify();
        Expression simpRight = add2.simplify();
        if (simpLeft instanceof Number && simpRight instanceof Number) {
            try {
                int sub = simpLeft.evalWithOnlyNumbers() - simpRight.evalWithOnlyNumbers();
                return new Number(sub);
            } catch (EvaluationException e) {
                throw new RuntimeException(e);
            }
        }

        if (simpLeft.equals(simpRight)) {
            return new Number(0);
        }
        if (isNumber(simpRight, 0)) {
            return simpLeft;
        }

        return new Sub(simpLeft, simpRight);
    }

    @Override
    public int evalWithOnlyNumbers() throws EvaluationException {
        return add1.evalWithOnlyNumbers() - add2.evalWithOnlyNumbers();
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
    public int eval(Map<String, Integer> context) throws EvaluationException {
        int leftValue = add1.eval(context);
        int rightValue = add2.eval(context);
        return leftValue - rightValue;
    }
}
