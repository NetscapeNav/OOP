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
    public void testUTF8() throws IOException {
        try {
            List<Long> results = SubstringSearch.find("src/test/resources/utf8.txt", "🌍");
            List<Long> expected = List.of(2L);
            assertEquals(expected, results);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testAbaba() throws IOException {
        try {
            List<Long> results = SubstringSearch.find("src/test/resources/ababa.txt", "aba");
            List<Long> expected = List.of(0L, 2L);
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
    public void testUnicode() throws IOException {
        String fileName = "unicode_test.txt";
        String content = "Hello 👋 world 🌍! こんにちは";
        String target = "こんにちは";

        File file = new File(fileName);

        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                new java.io.FileWriter(fileName, java.nio.charset.StandardCharsets.UTF_8))) {
            writer.write(content);
        }

        try {
            List<Long> results = SubstringSearch.find(fileName, target);
            List<Long> expected = List.of(19L);

            assertEquals(expected, results);
            assertEquals(1, results.size());
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
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

    @Test
    void testBuffer() throws IOException {
        String fileName = "buffer_boundary_test.txt";
        String pattern = "BOUNDARY";

        int bufferSize = 8192;
        int paddingLength = bufferSize - 3;

        StringBuilder content = new StringBuilder();
        for (int i = 0; i < paddingLength; i++) {
            content.append('a');
        }
        content.append(pattern);
        content.append("tail_content");

        File file = new File(fileName);

        try (java.io.BufferedWriter writer = new java.io.BufferedWriter(
                new java.io.FileWriter(fileName, java.nio.charset.StandardCharsets.UTF_8))) {
            writer.write(content.toString());
        }

        try {
            List<Long> results = SubstringSearch.find(fileName, pattern);
            List<Long> expected = List.of((long) paddingLength);

            assertEquals(expected, results, "Подстрока, пересекающая границу буфера (8192), должна быть найдена");
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }
    }
}
