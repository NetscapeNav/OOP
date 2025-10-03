package org.expression;

public class Mul extends Expression {
    private Expression add1;
    private Expression add2;

    public Mul(Expression x, Expression y) {
        add1 = x;
        add2 = y;
    }

    @Override
    public int method() {
        return add1.method() * add2.method();
    }

    @Override
    public void print() {
        System.out.print("(");
        add1.print();
        System.out.print(" * ");
        add2.print();
        System.out.print(")");
    }

    @Override
    public Expression derivative(String varName) {
        Expression leftDerivative = new Mul(add1.derivative(varName), add2);
        Expression rightDerivative = new Mul(add1, add2.derivative(varName));
        return new Add(leftDerivative, rightDerivative);
    }

    @Override
    public int eval(String equation) {
        int leftValue = add1.eval(equation);
        int rightValue = add2.eval(equation);
        return leftValue * rightValue;
    }
}
