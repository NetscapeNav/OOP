package org.example;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * Главный класс игры. Обеспечивает ход игры, раундом и следит за счётом.
 */
public class OchkoGame {
    private OchkoUI ui = new OchkoUI();
    private int wonPlayer = 0;
    private int wonDealer = 0;

    /**
     * Возврат числа выигрышей игроком.
     */
    public int getWonPlayer() {
        return wonPlayer;
    }

    /**
     * Возврат числа выигрышей дилера.
     */
    public int getWonDealer() {
        return wonDealer;
    }

    /**
     * Начало игры.
     */
    public void start() {
        ui.displayWelcomeMessage();
        int roundIndex = 0;
        while (true) {
            ui.displayRound(++roundIndex);
            round();
            ui.displayScore(wonPlayer, wonDealer);
        }
    }

    /**
     * Метод управления логики раунда игры.
     */
    private void round() {
        ArrayList<Card> deck = Deck.getDeck();
        Hand handPlayer = new Hand();
        Hand handDealer = new Hand();
        dealCards(deck, handPlayer, handDealer);
        if (checkBlackjack(handPlayer, handDealer)) {
            return;
        }
        turnPlayer(deck, handPlayer, handDealer);
        if (handPlayer.getScore() <= 21) {
            turnDealer(deck, handPlayer, handDealer);
        }
        showWinner(handDealer.getScore(), handPlayer.getScore());
    }

    private void dealCards(ArrayList<Card> deck, Hand handPlayer, Hand handDealer) {
        handPlayer.addCard(deck.remove(0));
        handPlayer.addCard(deck.remove(0));
        handDealer.addCard(deck.remove(0));
        handDealer.addCard(deck.remove(0));
        ui.displayCardsDealt();
    }

    private boolean checkBlackjack(Hand handPlayer, Hand handDealer) {
        if (handPlayer.getScore() == 21) {
            ui.displayHand("Your", handPlayer, false);
            ui.displayHand("Dealer", handDealer, true);
            ui.displayBlackjack(true);
            wonPlayer += 1;
            return true;
        }
        return false;
    }

    private void turnPlayer(ArrayList<Card> deck, Hand handPlayer, Hand handDealer) {
        ui.displayPlayerTurn();
        while (handPlayer.getScore() < 21) {
            ui.displayHand("Your", handPlayer, false);
            ui.displayHand("Dealer", handDealer, true);
            int decisionPlayer = ui.getPlayerDecision();

            if (decisionPlayer == 0) {
                break;
            } else if (decisionPlayer == 1) {
                handPlayer.addCard(deck.remove(0));
                if (handPlayer.getScore() >= 21) {
                    ui.displayHand("Your", handPlayer, false);
                    break;
                }
            } else {
                ui.displayInvalidInput();
            }
        }
    }

    private void turnDealer(ArrayList<Card> deck, Hand handPlayer, Hand handDealer) {
        ui.displayDealerTurn();
        ui.displayHand("Your", handPlayer, false);
        ui.displayHand("Dealer", handDealer, false);
        while (handDealer.getScore() < 17) {
            ui.displayDealerTakesCard();
            handDealer.addCard(deck.remove(0));
            ui.displayHand("Your", handPlayer, false);
            ui.displayHand("Dealer", handDealer, false);
        }
    }

    private void showWinner(int scoreDealer, int scorePlayer) {
        if (scorePlayer > 21) {
            ui.displayWinner(0);
            wonDealer += 1;
        } else if (scoreDealer > 21) {
            ui.displayWinner(2);
            wonPlayer += 1;
        } else if (scoreDealer > scorePlayer) {
            ui.displayWinner(0);
            wonDealer += 1;
        } else if (scoreDealer < scorePlayer) {
            ui.displayWinner(2);
            wonPlayer += 1;
        } else {
            ui.displayWinner(1);
        }
    }
}