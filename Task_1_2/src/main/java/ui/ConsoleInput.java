package ui;

import java.util.Scanner;

public class ConsoleInput implements AutoCloseable {
    private Scanner scanner;

    public ConsoleInput() {
        this.scanner = new Scanner(System.in);
    }

    public ConsoleInput(Scanner scanner) {
        this.scanner = scanner;
    }

    public int getPlayerDecision() {
        System.out.println("Enter \"1\", to take a card, and \"0\", to stop...");
        while (true) {
            String line = scanner.nextLine();
            if ("1".equals(line)) {
                return 1;
            } else if ("0".equals(line)) {
                return 0;
            } else {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }

    @Override
    public void close() {
        scanner.close();
        System.out.println("Scanner closed.");
    }
}
