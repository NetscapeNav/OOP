package elements;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TextTest {
    @Test
    void testText() {
        assertEquals("hello", new Text("hello").serialize());
    }

    @Test
    void testBold() {
        assertEquals("**bold**", new Text.Bold("bold").serialize());
    }

    @Test
    void testItalic() {
        assertEquals("*italic*", new Text.Italic("italic").serialize());
    }
    
    @Test
    void testSlashed() {
        assertEquals("~~slashed~~", new Text.Slashed("slashed").serialize());
    }
    
    @Test
    void testCode() {
        assertEquals("`code`", new Text.Code("code").serialize());
    }
    
    @Test
    void testCodeBlock() {
        String res = new Text.CodeBlock("line1\nline2").serialize();
        assertTrue(res.startsWith("```\n"));
        assertTrue(res.contains("line1\nline2"));
        assertTrue(res.endsWith("\n```"));
    }
}
