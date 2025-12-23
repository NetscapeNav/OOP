package org.expression;

import org.example.exception.EvaluationException;

import java.util.Map;

public class Div extends Expression {
    private final Expression add1;
    private final Expression add2;

    public Div(Expression x, Expression y) {
        add1 = x;
        add2 = y;
    }

    @Override
    public Expression simplify() {
        Expression simpLeft = add1.simplify();
        Expression simpRight = add2.simplify();
        if (simpLeft instanceof Number && simpRight instanceof Number) {
            try {
                int div = simpLeft.evalWithOnlyNumbers() / simpRight.evalWithOnlyNumbers();
                return new Number(div);
            } catch (EvaluationException e) {
                throw new RuntimeException(e);
            }
        }
        return new Div(simpLeft, simpRight);
    }

    @Override
    public int evalWithOnlyNumbers() throws EvaluationException {
        int rightValue = add2.evalWithOnlyNumbers();
        if (rightValue == 0) {
            throw new EvaluationException("Division by zero");
        }
        return add1.evalWithOnlyNumbers() / rightValue;
    }

    @Override
    public String print() {
        return String.format("(%s / %s)", add1.print(), add2.print());
    }

    @Override
    public Expression derivative(String varName) {
        // Производная частного: (u'v - uv') / v²
        Expression numerator1 = new Mul(add1.derivative(varName), add2);
        Expression numerator2 = new Mul(add1, add2.derivative(varName));
        Expression numerator = new Sub(numerator1, numerator2);
        Expression denominator = new Mul(add2, add2); // v²
        return new Div(numerator, denominator);
    }

    @Override
    public int eval(Map<String, Integer> context) throws EvaluationException {
        int leftValue = add1.eval(context);
        int rightValue = add2.eval(context);
        if (rightValue == 0) {
            throw new EvaluationException("Division by zero");
        }
        return leftValue / rightValue;
    }
}
