package elements;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OtherElementsTest {
    @Test
    void testHeader() {
        assertEquals("# Header 1", new Header(1, "Header 1").serialize());
        assertEquals("###### Header 6", new Header(6, "Header 6").serialize());
    }

    @Test
    void testQuote() {
        assertEquals("> Quote text", new Quote("Quote text").serialize());
    }

    @Test
    void testLink() {
        assertEquals("[W3C](https://www.w3.org/)", new Link("W3C", "https://www.w3.org/").serialize());
    }

    @Test
    void testImage() {
        assertEquals("![Alt](img.png)", new Image("Alt", "img.png").serialize());
    }

    @Test
    void testTask() {
        assertEquals("- [ ] Todo", new Task("Todo", false).serialize());
        assertEquals("- [x] Done", new Task("Done", true).serialize());
    }
}
