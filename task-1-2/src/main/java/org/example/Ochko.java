package org.example;

import org.example.Card;
import org.example.Deck;
import org.example.Hand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class Ochko {
    private Scanner in = new Scanner(System.in);

    public void start() {
        System.out.println("Добро пожаловать в Блэкджек!");
        int roundIndex = 0;
        while (true) {
            System.out.println("Round " + (++roundIndex));
            round();
        }
    }

    private void round() {
        ArrayList<Card> deck = Deck.getDeck();

        Hand handPlayer = new Hand();
        Hand handDealer = new Hand();

        handPlayer.addCard(deck.remove(0));
        handPlayer.addCard(deck.remove(0));
        handDealer.addCard(deck.remove(0));
        handDealer.addCard(deck.remove(0));
        System.out.println("The cards have given");

        if (handPlayer.getScore() == 21) {
            System.out.println("Blackjack! You've won!");
            return;
        }
        if (handDealer.getScore() == 21) {
            System.out.println("Diler has blackjack! You've lost.");
            return;
        }

        System.out.println("Your turn");

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