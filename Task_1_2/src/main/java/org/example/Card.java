package org.example;

/**
 * Представляет одну игральную карту.
 */
public class Card {
    private int value;
    private char suit;

    /**
     * Создаёт карту с заданным значением и мастью.
     *
     * @param value Цифровое значение карты (например, 2, 10, 11 - Валет, 14 - Туз).
     * @param suit  Масть карты ('S' - пики (Spades), 'H' - червы (Hearts) и т.д.).
     */
    public Card(int value, char suit) {
        this.value = value;
        this.suit = suit;
    }

    /**
     * Позволяет узнать значение карты.
     *
     * @return Цифровое значение карты.
     */
    public int getValue() {
        return value;
    }

    /**
     * Позволяет узнать масть карты.
     *
     * @return Символ, обозначающий масть карты.
     */
    public char getSuit() {
        return suit;
    }

    /**
     * Превращает информацию о карте в строку.
     *
     * @return Строка вида "Queen of Hearts".
     */
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
