import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LargeFileGenerator {

    public static List<Long> generateFile(String fileName, String target, long sizeBytes) throws IOException {
        List<Long> trueIndices = new ArrayList<>();
        Random random = new Random();
        long currentSize = 0;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            while (currentSize < sizeBytes) {
                if (random.nextInt(1000) == 0) {
                    writer.write(target);
                    trueIndices.add(currentSize);
                    currentSize += target.length();
                } else {
                    String noise = "simple_text_for_padding_";
                    writer.write(noise);
                    currentSize += noise.length();
                }
            }
        }

        return trueIndices;
    }
}