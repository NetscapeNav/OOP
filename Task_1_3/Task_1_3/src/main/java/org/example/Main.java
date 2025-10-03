package org.example;

import org.expression.*;

public class Main {
    public static void main(String[] args) {
        Expression e = new Add(new org.expression.Number(3), new Mul(new org.expression.Number(2), new Variable("x")));
        e.print();
        System.out.println();
        Expression de = e.derivative("x");
        de.print();
        System.out.println();
        int result = e.eval("x = 10; y = 13");
        System.out.println(result);
    }
}