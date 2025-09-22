package org.example;

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
        char[] suits = {'S', 'H', 'C', 'D'};
        int index = 0;
        for (int i = 2; i <= 14; i++) {
            for (char suit : suits) {
                deck.add(new Card(i, suit));
            }
        }
        shuffle(deck);
        return deck;
    }

    private static void shuffle(ArrayList<Card> deck) {
        Collections.shuffle(deck);
    }
}
