package game.model;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Класс для работы с колодой карт.
 */
public class Deck {
    /**
     * Создаёт стандартную колоду из 52 карт и перемешивает её.
     *
     * @return Перемешанный список с объектами карт.
     */
    public static ArrayList<Card> getDeck() {
        ArrayList<Card> deck = new ArrayList<Card>();
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.add(new Card(rank, suit));
            }
        }
        shuffle(deck);
        return deck;
    }

    private static void shuffle(ArrayList<Card> deck) {
        Collections.shuffle(deck);
    }
}
