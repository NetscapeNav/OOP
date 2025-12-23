package org.expression;

import org.example.exception.EvaluationException;

import java.util.Map;

public class Mul extends Expression {
    private final Expression add1;
    private final Expression add2;

    public Mul(Expression x, Expression y) {
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
                int mul = simpLeft.evalWithOnlyNumbers() * simpRight.evalWithOnlyNumbers();
                return new Number(mul);
            } catch (EvaluationException e) {
                throw new RuntimeException(e);
            }
        }

        if (isNumber(simpLeft, 0) || isNumber(simpRight, 0)) {
            return new Number(0);
        }

        if (isNumber(simpLeft, 1)) {
            return simpRight;
        }
        if (isNumber(simpRight, 1)) {
            return simpLeft;
        }

        return new Mul(simpLeft, simpRight);
    }

    @Override
    public int evalWithOnlyNumbers() throws EvaluationException {
        return add1.evalWithOnlyNumbers() * add2.evalWithOnlyNumbers();
    }

    @Override
    public String print() {
        return String.format("(%s * %s)", add1.print(), add2.print());
    }

    @Override
    public Expression derivative(String varName) {
        Expression leftDerivative = new Mul(add1.derivative(varName), add2);
        Expression rightDerivative = new Mul(add1, add2.derivative(varName));
        return new Add(leftDerivative, rightDerivative);
    }

    @Override
    public int eval(Map<String, Integer> context) throws EvaluationException {
        int leftValue = add1.eval(context);
        int rightValue = add2.eval(context);
        return leftValue * rightValue;
    }
}
