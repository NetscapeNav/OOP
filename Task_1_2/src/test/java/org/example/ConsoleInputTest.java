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
    void testDefaultConstructor() {
        System.setIn(new java.io.ByteArrayInputStream("0".getBytes()));
        try (ConsoleInput input = new ConsoleInput()) {
            assertNotNull(input);
        } finally {
            System.setIn(System.in);
        }
    }
}
