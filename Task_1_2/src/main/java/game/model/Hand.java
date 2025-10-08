package game.model;

import ui.ConsoleView;
import ui.LocalizationManager;

import java.util.ArrayList;

/**
 * Класс, представляющий руку карт.
 */
public class Hand {
    private ArrayList<Card> hand = new ArrayList<>();

    public void addCard(Card card) {
        hand.add(card);
    }

    public int getCardCount() {
        return hand.size();
    }

    /**
     * Показывает карты в руке. Может скрыть первую карту (для дилера).
     *
     * @param firstClosed Если true, первая карта скрывается. Если false, показываются все.
     * @return Строка, представляющая руку.
     */
    public String showHand(boolean firstClosed) {
        if (firstClosed) {
            if (hand.isEmpty()) return "[]";
            String closedCardStr = LocalizationManager.getString("closedcard");
            return "[" + hand.get(0).toString() + ", <" + closedCardStr + ">]";
        } else {
            // Используем переопределенный toString для полного отображения
            return this.toString();
        }
    }

    /**
     * Считает количество очков в руке по правилам игры.
     * Тузы считаются за 11, но если это приводит к перебору, то за 1.
     *
     * @return Итоговое количество очков.
     */
    public int getScore() {
        int score = 0;
        int aceCount = 0;
        for (Card card : hand) {
            score += card.getValue();
            if (card.getRank() == Rank.ACE) {
                aceCount++;
            }
        }
        while (score > 21 && aceCount > 0) {
            score -= 10;
            aceCount--;
        }
        return score;
    }

    /**
     * Переопределяем стандартный метод toString для красивого вывода.
     */
    @Override
    public String toString() {
        return hand.toString() + " => (" + getScore() + ")";
    }
}