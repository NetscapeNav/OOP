package org.example;

import game.model.Card;
import game.model.Deck;
import game.model.Hand;
import ui.ConsoleInput;
import ui.ConsoleView;

import java.util.ArrayList;

/**
 * Главный класс игры. Обеспечивает ход игры, раундом и следит за счётом.
 */
public class OchkoGame {
    private ConsoleView view;
    private ConsoleInput input;
    private int wonPlayer = 0;
    private int wonDealer = 0;

    /**
     * Конструктор для обычной игры.
     */
    public OchkoGame(ConsoleView view, ConsoleInput input) {
        this.view = view;
        this.input = input;
    }

    public int getWonPlayer() {
        return wonPlayer;
    }

    public int getWonDealer() {
        return wonDealer;
    }

    /**
     * Начало игры.
     */
    public void start() {
        view.displayWelcomeMessage();
        int roundIndex = 0;
        while (true) {
            view.displayRound(++roundIndex);
            round();
            view.displayScore(wonPlayer, wonDealer);
        }
    }

    /**
     * Приватный метод-загрузчик.
     */
    private void round() {
        round(Deck.getDeck());
    }

    /**
     * Метод управления логики раунда игры.
     *
     * @param deck Колода карт для игры.
     */
    void round(ArrayList<Card> deck) {
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
        view.displayCardsDealt();
    }

    private boolean checkBlackjack(Hand handPlayer, Hand handDealer) {
        if (handPlayer.getScore() == 21) {
            view.displayHand("Your", handPlayer, false);
            view.displayHand("Dealer", handDealer, true);
            view.displayBlackjack(true);
            wonPlayer += 1;
            return true;
        }
        return false;
    }

    private void turnPlayer(ArrayList<Card> deck, Hand handPlayer, Hand handDealer) {
        view.displayPlayerTurn();
        while (handPlayer.getScore() < 21) {
            view.displayHand("Your", handPlayer, false);
            view.displayHand("Dealer", handDealer, true);
            int decisionPlayer = input.getPlayerDecision();

            if (decisionPlayer == 0) {
                break;
            } else if (decisionPlayer == 1) {
                handPlayer.addCard(deck.remove(0));
                if (handPlayer.getScore() >= 21) {
                    view.displayHand("Your", handPlayer, false);
                    break;
                }
            } else {
                view.displayInvalidInput();
            }
        }
    }

    private void turnDealer(ArrayList<Card> deck, Hand handPlayer, Hand handDealer) {
        view.displayDealerTurn();
        view.displayHand("Your", handPlayer, false);
        view.displayHand("Dealer", handDealer, false);
        while (handDealer.getScore() < 17) {
            view.displayDealerTakesCard();
            handDealer.addCard(deck.remove(0));
            view.displayHand("Your", handPlayer, false);
            view.displayHand("Dealer", handDealer, false);
        }
    }

    private void showWinner(int scoreDealer, int scorePlayer) {
        if (scorePlayer > 21) {
            view.displayWinner(0);
            wonDealer += 1;
        } else if (scoreDealer > 21) {
            view.displayWinner(2);
            wonPlayer += 1;
        } else if (scoreDealer > scorePlayer) {
            view.displayWinner(0);
            wonDealer += 1;
        } else if (scoreDealer < scorePlayer) {
            view.displayWinner(2);
            wonPlayer += 1;
        } else {
            view.displayWinner(1);
        }
    }
}