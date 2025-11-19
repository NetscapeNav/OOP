import file.SubstringSearch;
import org.example.Main;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubstringTest {
    @Test
    public void test() throws IOException {
        try {
            List<Long> results = SubstringSearch.find("src/test/resources/test.txt", "бра");
            List<Long> expected = List.of(1L, 8L);
            assertEquals(expected, results);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testNull() throws IOException {
        List<Long> resultsNull = SubstringSearch.find("src/test/resources/test.txt", null);
        assertEquals(Collections.emptyList(), resultsNull, "Null pattern should return empty list.");

        List<Long> resultsEmpty = SubstringSearch.find("src/test/resources/test.txt", "");
        assertEquals(Collections.emptyList(), resultsEmpty, "Empty pattern should return empty list.");
    }

    @Test
    public void testNoMatch() throws IOException {
        List<Long> resultsNoMatch = SubstringSearch.find("src/test/resources/test.txt", "тест");
        assertEquals(Collections.emptyList(), resultsNoMatch, "Substring not present should return empty list.");
    }

    @Test
    void testMainExecutionCoverage() {
        assertDoesNotThrow(() -> Main.main(new String[]{}));
    }
}
