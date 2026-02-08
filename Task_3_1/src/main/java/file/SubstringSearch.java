package file;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SubstringSearch {
    public static List<Long> find(String fileName, String substring) throws IOException {
        List<Long> results = new ArrayList<>();
        if (substring == null || substring.isEmpty()) {
            return results;
        }
        int[] pi = computePrefixFunction(substring);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8))) {
            int charCode;
            int patternIndex = 0;
            long fileIndex = 0;
            while ((charCode = reader.read()) != -1) {
                char c = (char) charCode;
                while (patternIndex > 0 && c != substring.charAt(patternIndex)) {
                    patternIndex = pi[patternIndex - 1];
                }
                if (c == substring.charAt(patternIndex)) {
                    patternIndex++;
                }
                if (patternIndex == substring.length()) {
                    results.add(fileIndex - substring.length() + 1);
                    patternIndex = pi[patternIndex - 1];
                }
                fileIndex++;
            }
        }

        return results;
    }

    private static int[] computePrefixFunction(String pattern) {
        int[] pi = new int[pattern.length()];
        int j = 0;
        for (int i = 1; i < pattern.length(); i++) {
            while (j > 0 && pattern.charAt(i) != pattern.charAt(j)) {
                j = pi[j-1];
            }
            if (pattern.charAt(i) == pattern.charAt(j)) {
                j++;
            }
            pi[i] = j;
        }
        return pi;
    }
}
