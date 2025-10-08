package org.example;

import org.junit.jupiter.api.Test;
import ui.ConsoleInput;
import ui.ConsoleView;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleViewTest {
    @Test
    void testViewDisplayWinnerMessages() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        try {
            ConsoleView view = new ConsoleView();
            view.displayWinner(2);
            assertTrue(outContent.toString().contains("You've won!"));
            outContent.reset();
            view.displayWinner(0);
            assertTrue(outContent.toString().contains("You've lost."));
            outContent.reset();
            view.displayWinner(1);
            assertTrue(outContent.toString().contains("It's a tie..."));
        } finally {
            System.setOut(originalOut);
        }
    }
}