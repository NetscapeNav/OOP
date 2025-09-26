package org.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UnitTest {

    @Test
    void testCardToString() {
        Card cardFiveOfSpades = new Card(5, 'S');
        assertEquals("5 of Spades", cardFiveOfSpades.toString());
        Card cardKingOfHearts = new Card(13, 'H');
        assertEquals("King of Hearts", cardKingOfHearts.toString());
        Card cardAceOfClubs = new Card(14, 'C');
        assertEquals("Ace of Clubs", cardAceOfClubs.toString());
    }

    @Test
    void testCardGetters() {
        Card card = new Card(10, 'D');
        assertEquals(10, card.getValue());
        assertEquals('D', card.getSuit());
    }

    @Test
    void testDeckContainsAllCards() {
        ArrayList<Card> deck = Deck.getDeck();
        int[] expectedValues = {2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14};
        char[] expectedSuits = {'S', 'H', 'C', 'D'};
        for (int value : expectedValues) {
            for (char suit : expectedSuits) {
                boolean found = deck.stream().anyMatch(card ->
                        card.getValue() == value && card.getSuit() == suit);
                assertTrue(found, "Card " + value + " of " + suit + " is not found");
            }
        }
    }

    @Test
    void testDeckCreation() {
        ArrayList<Card> deck = Deck.getDeck();
        assertEquals(52, deck.size());
    }

    @Test
    void testSimpleScore() {
        Hand hand = new Hand();
        hand.addCard(new Card(5, 'S'));
        hand.addCard(new Card(8, 'H'));
        assertEquals(13, hand.getScore());
    }

    @Test
    void testScoreWithFaceCards() {
        Hand hand = new Hand();
        hand.addCard(new Card(12, 'C'));
        hand.addCard(new Card(13, 'D'));
        assertEquals(20, hand.getScore());
    }

    @Test
    void testScoreWithAceAsEleven() {
        Hand hand = new Hand();
        hand.addCard(new Card(14, 'S'));
        hand.addCard(new Card(9, 'H'));
        assertEquals(20, hand.getScore());
    }

    @Test
    void testScoreWithAceAsOne() {
        Hand hand = new Hand();
        hand.addCard(new Card(14, 'S'));
        hand.addCard(new Card(8, 'H'));
        hand.addCard(new Card(5, 'C'));
        assertEquals(14, hand.getScore());
    }

    @Test
    void testScoreWithMultipleAces() {
        Hand hand = new Hand();
        hand.addCard(new Card(14, 'S'));
        hand.addCard(new Card(14, 'H'));
        assertEquals(12, hand.getScore());
    }

    @Test
    void testBlackjackExample() {
        Hand hand = new Hand();
        hand.addCard(new Card(14, 'D'));
        hand.addCard(new Card(11, 'C'));
        assertEquals(21, hand.getScore());
    }

    @Test
    void testBust() {
        Hand hand = new Hand();
        hand.addCard(new Card(10, 'S'));
        hand.addCard(new Card(10, 'H'));
        hand.addCard(new Card(10, 'C'));
        assertEquals(30, hand.getScore());
    }

    @Test
    void testHandCardCount() {
        Hand hand = new Hand();
        assertEquals(0, hand.getCardCount());
        hand.addCard(new Card(5, 'S'));
        assertEquals(1, hand.getCardCount());
        hand.addCard(new Card(10, 'H'));
        assertEquals(2, hand.getCardCount());
    }

    @Test
    void testHandShowHandClosed() {
        Hand hand = new Hand();
        hand.addCard(new Card(10, 'S'));
        hand.addCard(new Card(5, 'H'));
        assertDoesNotThrow(() -> hand.showHand(true));
    }

    @Test
    void testHandShowHandOpen() {
        Hand hand = new Hand();
        hand.addCard(new Card(10, 'S'));
        hand.addCard(new Card(5, 'H'));
        assertDoesNotThrow(() -> hand.showHand(false));
    }

    @Test
    void testEmptyHandScore() {
        Hand hand = new Hand();
        assertEquals(0, hand.getScore());
    }

    @Test
    void testPlayerBlackjack() {
        Scanner testScanner = new Scanner("");
        OchkoUI testUI = new OchkoUI(testScanner);
        OchkoGame game = new OchkoGame(testUI);
        ArrayList<Card> specificDeck = new ArrayList<>(Arrays.asList(
                new Card(14, 'S'), new Card(13, 'S'),
                new Card(5, 'H'), new Card(6, 'H')
        ));
        game.round(specificDeck);
        assertEquals(1, game.getWonPlayer());
        assertEquals(0, game.getWonDealer());
    }

    @Test
    void testPlayerBusts() {
        Scanner testScanner = new Scanner("1");
        OchkoUI testUi = new OchkoUI(testScanner);
        OchkoGame game = new OchkoGame(testUi);
        ArrayList<Card> specificDeck = new ArrayList<>(Arrays.asList(
                new Card(10, 'S'), new Card(8, 'S'),
                new Card(5, 'H'), new Card(6, 'H'),
                new Card(10, 'C')
        ));
        game.round(specificDeck);
        assertEquals(0, game.getWonPlayer());
        assertEquals(1, game.getWonDealer());
    }

    @Test
    void testDealerBusts() {
        Scanner testScanner = new Scanner("0");
        OchkoUI testUi = new OchkoUI(testScanner);
        OchkoGame game = new OchkoGame(testUi);
        ArrayList<Card> specificDeck = new ArrayList<>(Arrays.asList(
                new Card(10, 'S'), new Card(10, 'D'),
                new Card(10, 'H'), new Card(6, 'H'),
                new Card(13, 'C')
        ));
        game.round(specificDeck);
        assertEquals(1, game.getWonPlayer());
        assertEquals(0, game.getWonDealer());
    }

    @Test
    void testTieGame() {
        Scanner testScanner = new Scanner("0");
        OchkoUI testUi = new OchkoUI(testScanner);
        OchkoGame game = new OchkoGame(testUi);
        ArrayList<Card> specificDeck = new ArrayList<>(Arrays.asList(
                new Card(10, 'S'), new Card(9, 'D'),
                new Card(10, 'H'), new Card(9, 'H')
        ));
        game.round(specificDeck);
        assertEquals(0, game.getWonPlayer());
        assertEquals(0, game.getWonDealer());
    }

    @Test
    void testPlayerWinsByScore() {
        Scanner testScanner = new Scanner("0");
        OchkoUI testUi = new OchkoUI(testScanner);
        OchkoGame game = new OchkoGame(testUi);
        ArrayList<Card> specificDeck = new ArrayList<>(Arrays.asList(
                new Card(10, 'S'), new Card(10, 'H'),
                new Card(10, 'C'), new Card(9, 'D')
        ));
        game.round(specificDeck);
        assertEquals(1, game.getWonPlayer());
        assertEquals(0, game.getWonDealer());
    }

    @Test
    void testDealerBlackjack() {
        Scanner testScanner = new Scanner("0");
        OchkoUI testUi = new OchkoUI(testScanner);
        OchkoGame game = new OchkoGame(testUi);
        ArrayList<Card> specificDeck = new ArrayList<>(Arrays.asList(
                new Card(10, 'S'),
                new Card(8, 'C'),
                new Card(14, 'H'),
                new Card(11, 'D')
        ));
        game.round(specificDeck);
        assertEquals(0, game.getWonPlayer());
        assertEquals(1, game.getWonDealer());
    }
}