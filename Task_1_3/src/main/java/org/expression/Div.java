package org.expression;

public class Div extends Expression {
    private Expression add1;
    private Expression add2;

    public Div(Expression x, Expression y) {
        add1 = x;
        add2 = y;
    }

    @Override
    public int method() {
        return add1.method() / add2.method();
    }

    @Override
    public void print() {
        System.out.print("(");
        add1.print();
        System.out.print(" / ");
        add2.print();
        System.out.print(")");
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
    public int eval(String equation) {
        int leftValue = add1.eval(equation);
        int rightValue = add2.eval(equation);
        return leftValue / rightValue;
    }
}
