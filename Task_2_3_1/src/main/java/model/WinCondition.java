package model;

public class WinCondition implements WinConditionInterface {
    private final int targetLength;

    public WinCondition(int targetLength) {
        this.targetLength = targetLength;
    }

    public boolean isW(Snake snake) {
        return snake.length() >= targetLength;
    }
}
