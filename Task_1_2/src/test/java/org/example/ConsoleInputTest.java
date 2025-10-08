package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ui.ConsoleInput;
import ui.LocalizationManager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

public class ConsoleInputTest {
    @BeforeAll
    static void setup() {
        LocalizationManager.loadBundle(new Locale("ru"));
    }

    @Test
    void testInputInvalidInput() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        try (ConsoleInput testInput = new ConsoleInput(new Scanner("abc\n1"))) {
            int decision = testInput.getPlayerDecision();
            assertTrue(outContent.toString().contains("Неверный ввод! Введите 0 или 1."));
            assertEquals(1, decision);
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testInputZeroDecision() {
        try (ConsoleInput testInput = new ConsoleInput(new Scanner("0"))) {
            int decision = testInput.getPlayerDecision();
            assertEquals(0, decision);
        }
    }

    @Test
    void testInputOneDecision() {
        try (ConsoleInput testInput = new ConsoleInput(new Scanner("1"))) {
            int decision = testInput.getPlayerDecision();
            assertEquals(1, decision);
        }
    }

    @Test
    void testInputInvalidThenValid() {
        Scanner scanner = new Scanner("5\n2\nabc\n1");
        ConsoleInput testInput = new ConsoleInput(scanner);
        int decision = testInput.getPlayerDecision();
        assertEquals(1, decision);
        testInput.close();
    }
}
