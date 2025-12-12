import file.SubstringSearch;
import org.example.Main;
import java.io.File;
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

    @Test
    void testLargeFile() throws Exception {
        String fileName = "large_test_file.txt";
        String target = "findme";
        long size = 1024L * 1024 * 1024;

        File file = new File(fileName);

        try {
            System.out.println("Генерируем файл...");
            List<Long> expectedIndices = LargeFileGenerator.generateFile(fileName, target, size);
            System.out.println("Файл создан. Ожидается найти " + expectedIndices.size() + " вхождений.");

            List<Long> actualIndices = SubstringSearch.find(fileName, target);

            assertEquals(expectedIndices, actualIndices);
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
