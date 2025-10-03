import org.expression.*;
import org.expression.Number;
import org.expression.Variable;
import org.expression.Add;
import org.expression.Sub;
import org.expression.Mul;
import org.expression.Div;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnitTest {
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testNumber() {
        Number num = new Number(5);
        assertEquals(5, num.method());

        assertEquals(5, num.eval("x = 10"));

        Expression de1 = num.derivative("x");
        assertEquals(0, de1.eval("x = 10"));

        num.print();
        assertEquals("5", outputStream.toString());
    }

    @Test
    void testVariable() {
        Variable var = new Variable("x");
        assertEquals(10, var.eval("x = 10; y = 20"));
        assertEquals(5, var.eval("x = 5"));

        Expression de1 = var.derivative("x");
        assertEquals(1, de1.eval("x = 5"));

        Expression de2 = var.derivative("y");
        assertEquals(0, de2.eval("x = 5; y = 10"));

        var.print();
        assertEquals("x", outputStream.toString());
    }

    @Test
    void testAdd() {
        Number num1 = new Number(5);
        Number num2 = new Number(10);
        Add num = new Add(num1, num2);
        assertEquals(15, num.method());
        assertEquals(15, num.eval("x = 10"));

        Variable var = new Variable("x");
        num = new Add(num1, var);
        assertEquals(15, num.eval("x = 10"));

        Expression de = num.derivative("x");
        assertEquals(1, de.eval("x = 5"));

        num.print();
        assertEquals("(5 + x)", outputStream.toString());
    }

    @Test
    void testSub() {
        Number num1 = new Number(5);
        Number num2 = new Number(10);
        Sub num = new Sub(num1, num2);
        assertEquals(-5, num.method());

        Variable var = new Variable("x");
        num = new Sub(num1, var);
        assertEquals(-5, num.eval("x = 10"));

        Expression de = num.derivative("x");
        assertEquals(-1, de.eval("x = 5"));

        num.print();
        assertEquals("(5 - x)", outputStream.toString());
    }

    @Test
    void Mul() {
        Number num1 = new Number(5);
        Number num2 = new Number(10);
        Mul num = new Mul(num1, num2);
        assertEquals(50, num.method());

        Variable var = new Variable("x");
        num = new Mul(num1, var);
        assertEquals(50, num.eval("x = 10"));

        Expression de = num.derivative("x");
        assertEquals(5, de.eval("x = 5"));

        num.print();
        assertEquals("(5 * x)", outputStream.toString());
    }

    @Test
    void testDiv() {
        Number num1 = new Number(5);
        Number num2 = new Number(10);
        Div num = new Div(num2, num1);
        assertEquals(2, num.method());

        Variable var = new Variable("x");
        num = new Div(num2, var);
        assertEquals(2, num.eval("x = 5"));

        Expression de = num.derivative("x");
        assertEquals(0, de.eval("x = 5"));

        num.print();
        assertEquals("(10 / x)", outputStream.toString());
    }

}