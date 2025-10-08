package org.example;

import game.model.Card;
import game.model.Rank;
import game.model.Suit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardTest {
    @Test
    void testToString() {
        Card queenOfHearts = new Card(Rank.QUEEN, Suit.HEARTS);
        assertEquals("Queen of Hearts", queenOfHearts.toString());
        Card tenOfSpades = new Card(Rank.TEN, Suit.SPADES);
        assertEquals("10 of Spades", tenOfSpades.toString());
    }

    @Test
    void testGetValue() {
        Card ace = new Card(Rank.ACE, Suit.SPADES);
        assertEquals(11, ace.getValue());
        Card king = new Card(Rank.KING, Suit.CLUBS);
        assertEquals(10, king.getValue());
        Card seven = new Card(Rank.TWO, Suit.DIAMONDS);
        assertEquals(2, seven.getValue());
    }
}
