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
        String target = "FINDME";
        // Генерируем файл на 1 ГБ (можно меньше для отладки, например 100 МБ)
        long size = 1024L * 1024 * 1024;

        System.out.println("Генерация файла...");
        List<Long> expectedIndices = LargeFileGenerator.generateResource(fileName, target, size);
        System.out.println("Файл создан. Ожидаем найти " + expectedIndices.size() + " вхождений.");

        // Ваш класс и метод поиска
        SubstringSearch finder = new SubstringSearch();
        List<Long> actualIndices = finder.find(fileName, target);

        assertEquals(expectedIndices, actualIndices);

        // Удаляем файл (или используйте @TempDir JUnit 5)
        new File(fileName).delete();
    }
}
