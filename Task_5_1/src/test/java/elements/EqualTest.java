package elements;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EqualTest {
    @Test
    void testTextEquality() {
        Text t1 = new Text("abc");
        Text t2 = new Text("abc");
        Text t3 = new Text("def");

        assertEquality(t1, t2, t3);
    }

    @Test
    void testFormattedTextEquality() {
        Element t1 = new Text.Bold("abc");
        Element t2 = new Text.Bold("abc");
        Element t3 = new Text.Bold("def");
        Element t4 = new Text.Italic("abc");

        assertEquality(t1, t2, t3);
        assertNotEquals(t1, t4);

        assertEquality(new Text.CodeBlock("code"), new Text.CodeBlock("code"), new Text.CodeBlock("diff"));
    }

    @Test
    void testHeaderEquality() {
        Header h1 = new Header(1, "Title");
        Header h2 = new Header(1, "Title");
        Header h3 = new Header(2, "Title");
        Header h4 = new Header(1, "Other");

        assertEquality(h1, h2, h3);
        assertNotEquals(h1, h4);
    }

    @Test
    void testListEquality() {
        MDList l1 = new MDList(true, "A", "B");
        MDList l2 = new MDList(true, "A", "B");
        MDList l3 = new MDList(false, "A", "B");
        MDList l4 = new MDList(true, "A");

        assertEquality(l1, l2, l3);
        assertNotEquals(l1, l4);
    }

    @Test
    void testTaskEquality() {
        Task t1 = new Task("Do it", false);
        Task t2 = new Task("Do it", false);
        Task t3 = new Task("Do it", true);

        assertEquality(t1, t2, t3);
    }

    @Test
    void testLinkAndImageEquality() {
        Link l1 = new Link("text", "url");
        Link l2 = new Link("text", "url");
        Link l3 = new Link("diff", "url");

        assertEquality(l1, l2, l3);

        Image i1 = new Image("alt", "src");
        Image i2 = new Image("alt", "src");
        Image i3 = new Image("alt", "diff");

        assertEquality(i1, i2, i3);
    }

    @Test
    void testQuoteEquality() {
        Quote q1 = new Quote("text");
        Quote q2 = new Quote("text");
        Quote q3 = new Quote("diff");

        assertEquality(q1, q2, q3);
    }

    @Test
    void testElementToString() {
        Text text = new Text("Test");
        assertEquals("Test", text.toString());
    }

    private void assertEquality(Element e1, Element e2, Element diff) {
        assertEquals(e1, e1);
        assertEquals(e1, e2);
        assertEquals(e2, e1);
        assertEquals(e1.hashCode(), e2.hashCode());

        assertNotEquals(e1, diff);
        assertNotEquals(e1, null);
        assertNotEquals(e1, new Object());
    }
}
