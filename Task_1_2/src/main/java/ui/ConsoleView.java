package ui;

import game.model.Hand;

import java.io.PrintWriter;
import java.nio.charset.Charset;

public class ConsoleView {
    private final PrintWriter out;

    public ConsoleView() {
        this.out = new PrintWriter(System.out, true, Charset.forName("UTF-8"));
    }

    public void displayWelcomeMessage() {
        out.println(LocalizationManager.getString("welcome"));
    }

    public void displayRound(int roundIndex) {
        out.println(LocalizationManager.getString("round", roundIndex));
    }

    public void displayScore(int wonPlayer, int wonDealer) {
        out.println(LocalizationManager.getString("score", wonPlayer, wonDealer));
    }

    public void displayCardsDealt() {
        out.println(LocalizationManager.getString("cardsDealt"));
    }

    public void displayPlayerTurn() {
        out.println(LocalizationManager.getString("playerTurn"));
    }

    public void displayDealerTurn() {
        out.println(LocalizationManager.getString("dealerTurn"));
    }

    public void displayHand(String owner, Hand hand, boolean isDealerFirstTurn) {
        out.println(LocalizationManager.getString("ownerCards", owner));
        out.println(hand.getHandRepresentation(isDealerFirstTurn));
    }

    public void displayDealerTakesCard() {
        out.println(LocalizationManager.getString("dealerTakesCard"));
    }

    public void displayWinner(int roundWon) {
        if (roundWon == 0) {
            out.println(LocalizationManager.getString("playerLost"));
        } else if (roundWon == 1) {
            out.println(LocalizationManager.getString("tie"));
        } else {
            out.println(LocalizationManager.getString("playerWon"));
        }
    }

    public void displayBlackjack(boolean playerWon) {
        if (playerWon) {
            out.println(LocalizationManager.getString("blackjackWin"));
        } else {
            out.println(LocalizationManager.getString("blackjackLoss"));
        }
    }

    public void displayInvalidInput() {
        out.println(LocalizationManager.getString("invalidInput"));
    }
}