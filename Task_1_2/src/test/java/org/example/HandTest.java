package org.example;

import game.model.Card;
import game.model.Hand;
import game.model.Rank;
import game.model.Suit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HandTest {
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
        assertEquals(20, hand.getScore());
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
    void testHandRepresentation() {
        Hand hand = new Hand();
        hand.addCard(new Card(Rank.QUEEN, Suit.HEARTS));
        hand.addCard(new Card(Rank.SEVEN, Suit.CLUBS));
        String expectedPlayerView = "[Queen of Hearts, 7 of Clubs] => (17)";
        assertEquals(expectedPlayerView, hand.getHandRepresentation(false));
        String expectedDealerView = "[Queen of Hearts, <closed card>]";
        assertEquals(expectedDealerView, hand.getHandRepresentation(true));
    }
}
