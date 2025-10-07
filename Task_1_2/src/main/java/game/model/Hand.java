package game.model;

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
     */
    public void showHand(boolean firstClosed) {
        if (firstClosed) {
            System.out.println("[" + hand.get(0).toString() + ", <closed card>]");
        } else {
            String response = "";
            response += "[";
            for (int i = 0; i < hand.size(); i++) {
                response += hand.get(i).toString();
                if (i < hand.size() - 1) {
                    response += ", ";
                }
            }
            response += "] => (" + getScore() + ")";
            System.out.println(response);
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
}