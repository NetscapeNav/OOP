package model;

import java.util.Random;
import java.util.Set;

public class GameField {
    private final int width;
    private final int height;
    private final Random random;

    public GameField(int width, int height, Random random) {
        this.width = width;
        this.height = height;
        this.random = random;
    }

    public boolean isInBounds(Cell c) {
        return c.getX() >= 0 && c.getX() < width && c.getY() >= 0 && c.getY() < height;
    }

    public Cell getRandomFreeCell(Set<Cell> reserved) {
        int totalCells = width * height;
        if (reserved.size() >= totalCells) {
            return null;
        }

        Cell toChoose;
        do {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            toChoose = new Cell(x, y);
        } while (reserved.contains(toChoose));

        return toChoose;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }
}
