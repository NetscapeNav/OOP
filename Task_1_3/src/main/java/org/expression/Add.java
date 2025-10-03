package org.expression;

public class Add extends Expression {
    private Expression add1;
    private Expression add2;

    public Add(Expression x, Expression y) {
        add1 = x;
        add2 = y;
    }

    @Override
    public int method() {
        return add1.method() + add2.method();
    }

    @Override
    public void print() {
        System.out.print("(");
        add1.print();
        System.out.print(" + ");
        add2.print();
        System.out.print(")");
    }

    @Override
    public Expression derivative(String varName) {
        return new Add(add1.derivative(varName), add2.derivative(varName));
    }

    @Override
    public int eval(String equation) {
        int leftValue = add1.eval(equation);
        int rightValue = add2.eval(equation);
        return leftValue + rightValue;
    }
}
