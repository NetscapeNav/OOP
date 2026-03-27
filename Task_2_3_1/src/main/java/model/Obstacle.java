package model;

import java.util.HashSet;
import java.util.Set;

public class Obstacle {
    private final Set<Cell> cells;

    public Obstacle() {
        this.cells = new HashSet<>();
    }

    public Obstacle(Set<Cell> cells) {
        this.cells = cells;
    }

    public void addCell(Cell cell) {
        assert cells != null;
        cells.add(cell);
    }

    public boolean contains(Cell cell) {
        assert cells != null;
        return cells.contains(cell);
    }

    public Set<Cell> getCells() {
        return cells;
    }
}
