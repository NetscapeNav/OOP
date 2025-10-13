package org.example;

import game.model.Card;
import game.model.Deck;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;

class DeckTest {
    @Test
    void testGetDeckCreatesFullDeck() {
        ArrayList<Card> deck = Deck.getDeck();
        assertNotNull(deck);
        assertEquals(52, deck.size());
    }
}