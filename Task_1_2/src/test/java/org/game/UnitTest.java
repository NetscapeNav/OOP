package org.game;

import game.model.*;
import org.junit.jupiter.api.Test;
import ui.ConsoleInput;
import ui.ConsoleView;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class UnitTest {
    @Test
    void testSimpleScore() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.FIVE, Suit.SPADES));
        hand.addCard(new Card(Rank.EIGHT, Suit.HEARTS));
        assertEquals(13, hand.getScore());
    }

    @Test
    void testScoreWithFaceCards() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.QUEEN, Suit.CLUBS));
        hand.addCard(new Card(Rank.KING, Suit.DIAMONDS));
        assertEquals(20, hand.getScore());
    }

    @Test
    void testScoreWithAceAsEleven() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.ACE, Suit.SPADES));
        hand.addCard(new Card(Rank.NINE, Suit.HEARTS));
        assertEquals(20, hand.getScore());
    }

    @Test
    void testScoreWithAceAsOne() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.ACE, Suit.SPADES));
        hand.addCard(new Card(Rank.EIGHT, Suit.HEARTS));
        hand.addCard(new Card(Rank.FIVE, Suit.CLUBS));
        assertEquals(14, hand.getScore());
    }

    @Test
    void testScoreWithMultipleAces() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.ACE, Suit.SPADES));
        hand.addCard(new Card(Rank.ACE, Suit.HEARTS));
        assertEquals(12, hand.getScore());
    }

    @Test
    void testScoreWithTwoAcesAndNumber() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.ACE, Suit.SPADES));
        hand.addCard(new Card(Rank.NINE, Suit.DIAMONDS));
        hand.addCard(new Card(Rank.ACE, Suit.CLUBS));
        assertEquals(21, hand.getScore());
    }

    @Test
    void testBlackjackExample() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.ACE, Suit.DIAMONDS));
        hand.addCard(new Card(Rank.JACK, Suit.CLUBS));
        assertEquals(21, hand.getScore());
    }

    @Test
    void testPlayerBlackjack() {
        try (ConsoleInput testInput = new ConsoleInput(new Scanner(""))) {
            ConsoleView testView = new ConsoleView();
            OchkoGame game = new OchkoGame(testView, testInput);
            ArrayList<Card> specificDeck = new ArrayList<>(Arrays.asList(
                    new Card(Rank.ACE, Suit.SPADES), new Card(Rank.KING, Suit.SPADES),
                    new Card(Rank.FIVE, Suit.HEARTS), new Card(Rank.SIX, Suit.HEARTS)
            ));
            game.round(specificDeck);
            assertEquals(1, game.getWonPlayer());
            assertEquals(0, game.getWonDealer());
        }
    }

    @Test
    void testPlayerBusts() {
        try (ConsoleInput testInput = new ConsoleInput(new Scanner("1"))) {
            ConsoleView testView = new ConsoleView();
            OchkoGame game = new OchkoGame(testView, testInput);
            ArrayList<Card> specificDeck = new ArrayList<>(Arrays.asList(
                    new Card(Rank.TEN, Suit.SPADES), new Card(Rank.EIGHT, Suit.SPADES),
                    new Card(Rank.FIVE, Suit.HEARTS), new Card(Rank.SIX, Suit.HEARTS),
                    new Card(Rank.ACE, Suit.CLUBS)
            ));
            game.round(specificDeck);
            assertEquals(0, game.getWonPlayer());
            assertEquals(1, game.getWonDealer());
        }
    }

    @Test
    void testDealerBusts() {
        try (ConsoleInput testInput = new ConsoleInput(new Scanner("0"))) {
            ConsoleView testView = new ConsoleView();
            OchkoGame game = new OchkoGame(testView, testInput);
            ArrayList<Card> specificDeck = new ArrayList<>(Arrays.asList(
                    new Card(Rank.TEN, Suit.SPADES), new Card(Rank.TEN, Suit.DIAMONDS),
                    new Card(Rank.TEN, Suit.HEARTS), new Card(Rank.SIX, Suit.HEARTS),
                    new Card(Rank.QUEEN, Suit.CLUBS)
            ));
            game.round(specificDeck);
            assertEquals(1, game.getWonPlayer());
            assertEquals(0, game.getWonDealer());
        }
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
    void testInputInvalidInput() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        try (ConsoleInput testInput = new ConsoleInput(new Scanner("abc\n1"))) {
            int decision = testInput.getPlayerDecision();
            assertTrue(outContent.toString().contains("Invalid input!"));
            assertEquals(1, decision);
        } finally {
            System.setOut(originalOut);
        }
    }
}