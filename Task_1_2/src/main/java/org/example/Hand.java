package org.example;

import java.util.ArrayList;

public class Hand {
    private ArrayList<Card> hand = new ArrayList<>();

    public void addCard(Card card) {
        hand.add(card);
    }

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

    public int getScore() {
        int score = 0;
        int aceCount = 0;
        for (Card card : hand) {
            if (card.getValue() == 14) {
                aceCount++;
                score += 11;
            } else if (card.getValue() > 10) {
                score += 10;
                continue;
            } else {
                score += card.getValue();
            }
        }
        while (score > 21 && aceCount > 0) {
            score -= 10;
            aceCount--;
        }
        return score;
    }
}