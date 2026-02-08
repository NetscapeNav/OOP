import org.example.ExpressionParser;
import org.example.exception.EvaluationException;
import org.example.exception.ParsingException;
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

import static org.junit.jupiter.api.Assertions.*;

public class ParserAndExperssionTest {
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() {
        outputStream = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStream));
    }

    @Test
    void testNumber() throws Exception {
        Number num = new Number(5);
        assertEquals(5, num.evalWithOnlyNumbers());

        assertEquals(5, num.eval("x = 10"));

        Expression de1 = num.derivative("x");
        assertEquals(0, de1.eval("x = 10"));

        assertEquals("5", num.print());
    }

    @Test
    void testVariable() throws Exception {
        Variable var = new Variable("x");
        assertEquals(10, var.eval("x = 10; y = 20"));
        assertEquals(5, var.eval("x = 5"));
        assertEquals("x", var.print());

        Expression de1 = var.derivative("x");
        assertEquals(1, de1.eval("x = 5"));

        Expression de2 = var.derivative("y");
        assertEquals(0, de2.eval("x = 5; y = 10"));

        assertEquals("x", var.print());
    }

    @Test
    void testAdd() throws Exception {
        Number num1 = new Number(5);
        Number num2 = new Number(10);
        Add num = new Add(num1, num2);
        assertEquals(15, num.evalWithOnlyNumbers());
        assertEquals(15, num.eval("x = 10"));

        Variable var = new Variable("x");
        num = new Add(num1, var);
        assertEquals(15, num.eval("x = 10"));

        Expression de = num.derivative("x");
        assertEquals(1, de.eval("x = 5"));

        assertEquals("(5 + x)", num.print());
    }

    @Test
    void testSub() throws Exception {
        Number num1 = new Number(5);
        Number num2 = new Number(10);
        Sub num = new Sub(num1, num2);
        assertEquals(-5, num.evalWithOnlyNumbers());

        Variable var = new Variable("x");
        num = new Sub(num1, var);
        assertEquals(-5, num.eval("x = 10"));

        Expression de = num.derivative("x");
        assertEquals(-1, de.eval("x = 5"));

        assertEquals("(5 - x)", num.print());
    }

    @Test
    void Mul() throws Exception {
        Number num1 = new Number(5);
        Number num2 = new Number(10);
        Mul num = new Mul(num1, num2);
        assertEquals(50, num.evalWithOnlyNumbers());

        Variable var = new Variable("x");
        num = new Mul(num1, var);
        assertEquals(50, num.eval("x = 10"));

        Expression de = num.derivative("x");
        assertEquals(5, de.eval("x = 5"));

        assertEquals("(5 * x)", num.print());
    }

    @Test
    void testDiv() throws Exception {
        Number num1 = new Number(5);
        Number num2 = new Number(10);
        Div num = new Div(num2, num1);
        assertEquals(2, num.evalWithOnlyNumbers());

        Variable var = new Variable("x");
        num = new Div(num2, var);
        assertEquals(2, num.eval("x = 5"));

        Expression de = num.derivative("x");
        assertEquals(0, de.eval("x = 5"));

        assertEquals("(10 / x)", num.print());
    }

    @Test
    void testEvaluationExceptionCreation() {
        String errorMessage = "This is a test";
        EvaluationException exception = new EvaluationException(errorMessage);
        assertEquals(errorMessage, exception.getMessage());
    }

    @Test
    void testParseExpression() throws Exception {
        ExpressionParser parser = new ExpressionParser();

        Expression expr = parser.parse("(x + 5)");
        assertEquals(15, expr.eval("x = 10"));
        assertEquals("(x + 5)", expr.print());

        Exception e1 = assertThrows(ParsingException.class, () -> parser.parse("(x + 5) smth"));
        assertEquals("Unexpected characters at end of input: 'smth'", e1.getMessage());

        Exception e2 = assertThrows(ParsingException.class, () -> parser.parse("% 5"));
        assertEquals("Unexpected character: '%' at position 0", e2.getMessage());

        Exception e3 = assertThrows(ParsingException.class, () -> parser.parse("(x ^ 2)"));
        assertEquals("Expected operator (+, -, *, /) but found: '^' at position 3", e3.getMessage());
    }

    @Test
    void testSimplification() {
        Expression e1 = new Add(new Number(1), new Number(2));
        assertEquals("3", e1.simplify().print());

        Expression e2 = new Mul(new Variable("x"), new Number(0));
        assertEquals("0", e2.simplify().print());

        Expression e3 = new Mul(new Variable("x"), new Number(1));
        assertEquals("x", e3.simplify().print());

        Expression e4 = new Sub(new Variable("x"), new Variable("x"));
        assertEquals("0", e4.simplify().print());

        Expression e5 = new Mul(new Add(new Number(1), new Number(1)), new Variable("x"));
        assertEquals("(2 * x)", e5.simplify().print());

        Expression e6 = new Sub(new Variable("x"), new Number(0));
        assertEquals("x", e6.simplify().print());
    }

    @Test
    void testEqualsAndHashCode() {
        Number n1 = new Number(5);
        Number n2 = new Number(5);
        Number n3 = new Number(6);
        assertEquals(n1, n2);
        assertNotEquals(n1, n3);
        assertEquals(n1.hashCode(), n2.hashCode());
        assertNotEquals(n1, new Object());
        assertNotEquals(n1, null);

        Variable v1 = new Variable("x");
        Variable v2 = new Variable("x");
        Variable v3 = new Variable("y");
        assertEquals(v1, v2);
        assertNotEquals(v1, v3);
        assertEquals(v1.hashCode(), v2.hashCode());

        Add a1 = new Add(n1, v1);
        Add a2 = new Add(n1, v1);
        Add a3 = new Add(v1, n1);
        assertEquals(a1, a2);
        assertNotEquals(a1, a3);
        assertEquals(a1.hashCode(), a2.hashCode());

        Sub s1 = new Sub(n1, v1);
        Sub s2 = new Sub(n1, v1);
        Sub s3 = new Sub(v1, n1);
        assertEquals(s1, s2);
        assertNotEquals(s1, s3);
        assertEquals(s1.hashCode(), s2.hashCode());

        Mul m1 = new Mul(n1, v1);
        Mul m2 = new Mul(n1, v1);
        Mul m3 = new Mul(v1, n1);
        assertEquals(m1, m2);
        assertNotEquals(m1, m3);
        assertEquals(m1.hashCode(), m2.hashCode());

        Div d1 = new Div(n1, v1);
        Div d2 = new Div(n1, v1);
        Div d3 = new Div(v1, n1);
        assertEquals(d1, d2);
        assertNotEquals(d1, d3);
        assertEquals(d1.hashCode(), d2.hashCode());
    }

    @Test
    void testParserPrecedence() throws Exception {
        ExpressionParser parser = new ExpressionParser();

        Expression e = parser.parse("2 + 2 * 2");
        assertEquals(6, e.evalWithOnlyNumbers());

        Expression e2 = parser.parse("x + y - z");
        assertEquals("((x + y) - z)", e2.print());
    }
}