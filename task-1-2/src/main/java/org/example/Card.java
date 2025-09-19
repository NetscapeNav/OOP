package org.example;

public class Card {
    private int value;
    private char suit;

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

    public String toString() {
        String rank;
        String suit;

        switch (this.value) {
            case 11:
                rank = "Jack";
                break;
            case 12:
                rank = "Queen";
                break;
            case 13:
                rank = "King";
                break;
            case 14:
                rank = "Ace";
                break;
            default:
                rank = String.valueOf(this.value);
        }

        switch (this.suit) {
            case 'S':
                suit = "Spades";
                break;
            case 'H':
                suit = "Hearts";
                break;
            case 'C':
                suit = "Clubs";
                break;
            case 'D':
                suit = "Diamonds";
                break;
            default:
                suit = "?";
        }

        return rank + " of " + suit;
    }
}
