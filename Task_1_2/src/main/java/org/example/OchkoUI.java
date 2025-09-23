package org.example;

import java.util.Scanner;

/**
 * Класс для взаимодействия с пользователем через консоль.
 */
public class OchkoUI {
    private final Scanner in = new Scanner(System.in);

    /*
    *
    */
    public void displayWelcomeMessage() {
        System.out.println("Добро пожаловать в Блэкджек!");
    }

    /*
     *
     */
    public void displayRound(int roundIndex) {
        System.out.println("Round " + roundIndex);
    }

    /*
     *
     */
    public void displayScore(int wonPlayer, int wonDealer) {
        System.out.println("It's " + wonPlayer + ":" + wonDealer);
    }

    /*
     *
     */
    public void displayCardsDealt() {
        System.out.println("The cards have been given");
    }

    /*
     *
     */
    public void displayPlayerTurn() {
        System.out.println("-----------------");
        System.out.println("Your turn");
    }

    /*
     *
     */
    public void displayDealerTurn() {
        System.out.println("-----------------");
        System.out.println("Dealer's turn");
    }

    /*
     *
     */
    public void displayHand(String owner, Hand hand, boolean isDealerFirstTurn) {
        System.out.println(owner + "'s cards:");
        hand.showHand(isDealerFirstTurn);
    }

    /*
     *
     */
    public int getPlayerDecision() {
        System.out.println("Enter \"1\", to take a card, and \"0\", to stop...");
        while (!in.hasNextInt()) {
            System.out.println("Invalid input! Please enter a number.");
            in.next();
        }
        return in.nextInt();
    }

    /*
     *
     */
    public void displayInvalidInput() {
        System.out.println("Invalid input! Type 0 or 1.");
    }

    /*
     *
     */
    public void displayDealerTakesCard() {
        System.out.println("Dealer takes a card...");
    }

    /*
     *
     */
    public void displayWinner(int roundWon) {
        if (roundWon == 0) {
            System.out.println("You've lost.");
        } else if (roundWon == 1) {
            System.out.println("It's a tie...");
        } else {
            System.out.println("You've won!");
        }
    }

    /*
     *
     */
    public void displayBlackjack(boolean playerWon) {
        if (playerWon) {
            System.out.println("Blackjack! You've won!");
        } else {
            System.out.println("Diler has a blackjack. You've lost.");
        }
    }
}