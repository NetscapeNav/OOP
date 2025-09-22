package org.example;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class UnitTest {
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
    void testBlackjack() {
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
}