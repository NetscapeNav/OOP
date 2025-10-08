package ui;

import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Scanner;

public class ConsoleInput implements AutoCloseable {
    private Scanner scanner;
    private final PrintWriter out;

    public ConsoleInput() {
        this.scanner = new Scanner(System.in);
        this.out = new PrintWriter(System.out, true, Charset.forName("UTF-8"));
    }

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
        this.out = new PrintWriter(System.out, true, Charset.forName("UTF-8"));
    }

    public int getPlayerDecision() {
        out.println(LocalizationManager.getString("playerPrompt"));
        while (true) {
            String line = scanner.nextLine();
            if ("1".equals(line)) {
                return 1;
            } else if ("0".equals(line)) {
                return 0;
            } else {
                out.println(LocalizationManager.getString("invalidInput"));
            }
        }
    }

    @Override
    public void close() {
        scanner.close();
    }
}
