package org.example;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Ochko {
    public static void start() {
        System.out.println("Добро пожаловать в Блэкджек!");
        int roundIndex = 0;
        while (true) {
            System.out.println("Round " + (++roundIndex));
            round();
        }
    }

    private static void round() {
        ArrayList<Card> deck = Deck.getDeck();

        Hand handPlayer = new Hand();
        Hand handDealer = new Hand();

        handPlayer.addCard(deck.remove(0));
        handPlayer.addCard(deck.remove(0));
        handDealer.addCard(deck.remove(0));
        handDealer.addCard(deck.remove(0));
        System.out.println("The cards have given");

        System.out.println("Your turn");

        Scanner in = new Scanner(System.in);
        boolean playerDecision = true;
        int scorePlayer = 0;
        int scoreDealer = 0;

        while (playerDecision) {
            System.out.println("Your cards:");
            handPlayer.showHand(false);
            scorePlayer = handPlayer.getScore();
            System.out.println("Dealer's cards:");
            handDealer.showHand(true);
            scoreDealer = handDealer.getScore();

            if (handPlayer.getScore() >= 21) {
                playerDecision = false;
                continue;
            }

            System.out.println("Enter \"1\", to take a card, and \"0\", to stop...");
            int decisionPlayer = in.nextInt();

            if (decisionPlayer == 0) {
                playerDecision = false;
            } else if (decisionPlayer == 1) {
                handPlayer.addCard(deck.remove(0));
            } else {
                System.out.println("Invalid input! Type 0 or 1.");
            }
        }

        if (handPlayer.getScore() <= 21) {
            System.out.println("Dealer's turn");
            handDealer.showHand(false);
            while (handDealer.getScore() < 17) {
                System.out.println("Dealer takes a card...");
                handDealer.addCard(deck.remove(0));
                handDealer.showHand(false);
                scoreDealer = handDealer.getScore();

                if (handDealer.getScore() > 21) {
                    break;
                }
            }
        }

        if (scorePlayer > 21) {
            System.out.println("You've lost.");
        } else if (scorePlayer == 21) {
            System.out.println("You've won!");
        } else if (scoreDealer > 21) {
            System.out.println("You've won!");
        } else if (scoreDealer == 21) {
            System.out.println("You've lost.");
        } else if (scoreDealer > scorePlayer) {
            System.out.println("You've lost.");
        } else if (scoreDealer < scorePlayer) {
            System.out.println("You've won!");
        } else {
            System.out.println("It's a tie...");
        }
    }
}

class Hand {
    private ArrayList<Card> hand = new ArrayList<>();

    public void addCard(Card card) {
        hand.add(card);
    }

    public void showHand(boolean firstClosed) {
        if (firstClosed) {
            Card firstCard = hand.get(0);
            System.out.println("[" + "Card: " + firstCard.getValue() + ", suit: " + firstCard.getSuit() + ", <closed card>]");
        } else {
            String response = "";
            response += "[";
            for (Card card : hand) {
                response += "Card: " + card.getValue() + ", suit: " + card.getSuit() + ", ";
            }
            response += "] => (" + getScore() + ")";
            System.out.println(response);
        }
    }

    public int getScore() {
        int score = 0;
        int aceCount = 0;
        for (Card card : hand) {
            if (card.value == 14) {
                aceCount++;
            } else if (card.value > 10) {
                score += 10;
                continue;
            }
            score += card.getValue();
        }
        while (score > 21 && aceCount > 0) {
            score -= 10;
            aceCount--;
        }
        return score;
    }
}

class Card {
    int value;
    char suit;

    public Card(int value, char suit) {
        this.value = value;
        this.suit = suit;
    }

    public int getValue() {
        return value;
    }
    public char getSuit() {
        return suit;
    }
}

class Deck {
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