import org.expression.*;
import org.expression.Number;
import org.expression.Variable;
import org.expression.Add;
import org.expression.Sub;
import org.expression.Mul;
import org.expression.Div;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UnitTest {
    @Test
    void testNumber() {
        Number num = new Number(5);
        assertEquals(5, num.method());

        assertEquals(5, num.eval("x = 10"));
    }
}