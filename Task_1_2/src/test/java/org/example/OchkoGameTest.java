package org.example;

import game.model.Card;
import game.model.Rank;
import game.model.Suit;
import org.junit.jupiter.api.Test;
import ui.ConsoleInput;
import ui.ConsoleView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OchkoGameTest {
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
        try (ConsoleInput testInput = new ConsoleInput(new Scanner("1\n0\n"))) {
            ConsoleView testView = new ConsoleView();
            OchkoGame game = new OchkoGame(testView, testInput);
            ArrayList<Card> specificDeck = new ArrayList<>(Arrays.asList(
                    new Card(Rank.TEN, Suit.SPADES), new Card(Rank.EIGHT, Suit.SPADES),
                    new Card(Rank.FIVE, Suit.HEARTS), new Card(Rank.SIX, Suit.HEARTS),
                    new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.KING, Suit.DIAMONDS)
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
    void testPlayerWins() {
        try (ConsoleInput testInput = new ConsoleInput(new Scanner("1\n0\n"))) {
            ConsoleView testView = new ConsoleView();
            OchkoGame game = new OchkoGame(testView, testInput);
            ArrayList<Card> specificDeck = new ArrayList<>(Arrays.asList(
                    new Card(Rank.TEN, Suit.SPADES), new Card(Rank.EIGHT, Suit.SPADES),
                    new Card(Rank.TWO, Suit.HEARTS), new Card(Rank.SIX, Suit.HEARTS),
                    new Card(Rank.ACE, Suit.CLUBS), new Card(Rank.KING, Suit.DIAMONDS)
            ));
            game.round(specificDeck);
            assertEquals(1, game.getWonPlayer());
            assertEquals(0, game.getWonDealer());
        }
    }
}
