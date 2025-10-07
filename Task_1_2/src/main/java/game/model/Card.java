package game.model;

/**
 * Класс представляет одну игральную карту.
 */
public class Card {
    private Rank rank;
    private Suit suit;

    /**
     * Создаёт карту с заданным значением и мастью.
     *
     * @param rank Цифровое значение карты (например, 2, 10, 11 - Валет, 14 - Туз).
     * @param suit  Масть карты ('S' - пики (Spades), 'H' - червы (Hearts) и т.д.).
     */
    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public int getValue() {
        return rank.getValue();
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    /**
     * Превращает информацию о карте в строку.
     *
     * @return Строка вида "Queen of Hearts".
     */
    public String toString() {
        String rankStr;
        String suitStr;

        switch (this.rank) {
            case JACK: rankStr = "Jack"; break;
            case QUEEN: rankStr = "Queen"; break;
            case KING: rankStr = "King"; break;
            case ACE: rankStr = "Ace"; break;
            default:
                rankStr = String.valueOf(this.rank.getValue());
        }

        switch (this.suit) {
            case SPADES: suitStr = "Spades"; break;
            case HEARTS: suitStr = "Hearts"; break;
            case CLUBS: suitStr = "Clubs"; break;
            case DIAMONDS: suitStr = "Diamonds"; break;
            default: suitStr = "?";
        }

        return rankStr + " of " + suitStr;
    }
}

