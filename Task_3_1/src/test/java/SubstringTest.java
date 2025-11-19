import file.SubstringSearch;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SubstringTest {
    @Test
    public void test() {
        try {
            SubstringSearch finder = new SubstringSearch();
            List<Long> results = SubstringSearch.find("src/test/resources/test.txt", "бра");
            List<Long> expected = List.of(1L, 8L);
            assertEquals(expected, results);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
