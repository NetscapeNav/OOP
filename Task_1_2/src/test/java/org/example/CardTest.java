package org.example;

import java.util.Locale;
import game.model.Card;
import game.model.Rank;
import game.model.Suit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ui.LocalizationManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardTest {
    @BeforeAll
    static void setup() {
        LocalizationManager.loadBundle(new Locale("ru"));
    }

    @Test
    void testToString() {
        Card queenOfHearts = new Card(Rank.QUEEN, Suit.HEARTS);
        assertEquals("Дама Червей", queenOfHearts.toString());
        Card tenOfSpades = new Card(Rank.TEN, Suit.SPADES);
        assertEquals("10 Пик", tenOfSpades.toString());
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
