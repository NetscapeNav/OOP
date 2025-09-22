package org.example;

import java.awt.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Главный класс игры. Обеспечивает ход игры, раундом и следит за счётом.
 */
public class Ochko {
    private Scanner in = new Scanner(System.in);
    private int wonPlayer = 0;
    private int wonDealer = 0;

    /**
     * Начало игры.
     */
    public void start() {
        System.out.println("Добро пожаловать в Блэкджек!");
        int roundIndex = 0;
        while (true) {
            System.out.println("Round " + (++roundIndex));
            round();
            System.out.println("It's " + wonPlayer + ":" + wonDealer);
            /*System.out.println("Still wanna play? (0 - no, 1 - yes)");
            if (in.nextInt() == 0) {
                break;
            }*/
        }
        //System.out.println("Goodbye!");
    }

    /**
     * Метод управления логики раунда игры.
     */
    private void round() {
        ArrayList<Card> deck = Deck.getDeck();
        Hand handPlayer = new Hand();
        Hand handDealer = new Hand();
        dealCards(deck, handPlayer, handDealer);
        if (checkBlackjack(handPlayer, handDealer, true)) {
            return;
        }
        turnPlayer(deck, handPlayer, handDealer);
        if (checkBlackjack(handPlayer, handDealer, false)) {
            return;
        }
        if (handPlayer.getScore() <= 21) {
            turnDealer(deck, handDealer);
        }
        showWinner(handDealer.getScore(), handPlayer.getScore());
    }

    private void dealCards(ArrayList<Card> deck, Hand handPlayer, Hand handDealer) {
        handPlayer.addCard(deck.remove(0));
        handPlayer.addCard(deck.remove(0));
        handDealer.addCard(deck.remove(0));
        handDealer.addCard(deck.remove(0));
        System.out.println("The cards have given");
    }

    private boolean checkBlackjack(Hand handPlayer, Hand handDealer, boolean isPlayer) {
        boolean blackjackPlayer = (handPlayer.getScore() == 21);
        boolean blackjackDealer = (handDealer.getScore() == 21);
        if (blackjackPlayer && isPlayer) {
            System.out.println("Your cards:");
            handPlayer.showHand(false);
            System.out.println("Dealer's cards:");
            handDealer.showHand(true);
            System.out.println("Blackjack! You've won!");
            wonPlayer += 1;
            return true;
        } else if (blackjackDealer && !isPlayer) {
            System.out.println("Your cards:");
            handPlayer.showHand(false);
            System.out.println("Dealer's cards:");
            handDealer.showHand(false);
            System.out.println("Diler has blackjack! You've lost.");
            wonDealer += 1;
            return true;
        }
        return false;
    }

    private void turnPlayer(ArrayList<Card> deck, Hand handPlayer, Hand handDealer) {
        System.out.println("Your turn");
        while (true) {
            System.out.println("Your cards:");
            handPlayer.showHand(false);
            System.out.println("Dealer's cards:");
            handDealer.showHand(true);
            if (handPlayer.getScore() >= 21) {
                break;
            }
            System.out.println("Enter \"1\", to take a card, and \"0\", to stop...");
            int decisionPlayer = in.nextInt();
            if (decisionPlayer == 0) {
                break;
            } else if (decisionPlayer == 1) {
                handPlayer.addCard(deck.remove(0));
            } else {
                System.out.println("Invalid input! Type 0 or 1.");
            }
        }
    }

    private void turnDealer(ArrayList<Card> deck, Hand handDealer) {
        System.out.println("Dealer's turn");
        handDealer.showHand(false);
        while (handDealer.getScore() < 17) {
            System.out.println("Dealer takes a card...");
            handDealer.addCard(deck.remove(0));
            handDealer.showHand(false);
            if (handDealer.getScore() > 21) {
                break;
            }
        }
    }

    private void showWinner(int scoreDealer, int scorePlayer) {
        if (scorePlayer > 21) {
            System.out.println("You've lost.");
            wonDealer += 1;
        } else if (scorePlayer == 21) {
            System.out.println("You've won!");
            wonPlayer += 1;
        } else if (scoreDealer > 21) {
            System.out.println("You've won!");
            wonPlayer += 1;
        } else if (scoreDealer == 21) {
            System.out.println("You've lost.");
            wonDealer += 1;
        } else if (scoreDealer > scorePlayer) {
            System.out.println("You've lost.");
            wonDealer += 1;
        } else if (scoreDealer < scorePlayer) {
            System.out.println("You've won!");
            wonPlayer += 1;
        } else {
            System.out.println("It's a tie...");
        }
    }
}