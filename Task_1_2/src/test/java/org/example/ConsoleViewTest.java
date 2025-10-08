package org.example;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ui.ConsoleInput;
import ui.ConsoleView;
import ui.LocalizationManager;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ConsoleViewTest {
    @BeforeAll
    static void setup() {
        LocalizationManager.loadBundle(new Locale("ru"));
    }

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

    @Test
    void testDisplayRoundMessage() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        try {
            ConsoleView view = new ConsoleView();
            view.displayRound(1);
            assertTrue(outContent.toString().contains("Round 1"));
            outContent.reset();
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testDisplayScoreMessage() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        try {
            ConsoleView view = new ConsoleView();
            view.displayScore(3, 2);
            assertTrue(outContent.toString().contains("3:2"));
            outContent.reset();
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void testDisplayDealtMessage() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        try {
            ConsoleView view = new ConsoleView();
            view.displayCardsDealt();
            assertTrue(outContent.toString().contains("The cards have been given"));
            outContent.reset();
        } finally {
            System.setOut(originalOut);
        }
    }
}