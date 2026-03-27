package model;

import java.util.*;

public class Snake {
    private final Deque<Cell> body;
    private Direction direction;
    private boolean alive;
    private int pendingGrowth;

    public Snake(Deque<Cell> body, Direction direction, boolean alive, int pendingGrowth) {
        this.body = body;
        this.direction = direction;
        this.alive = alive;
        this.pendingGrowth = pendingGrowth;
    }

    public Cell getHead() {
        return body.getFirst();
    }

    public Cell getTail() {
        return body.getLast();
    }

    public Deque<Cell> getBody(){
        return body;
    }

    public int length() {
        return body.size();
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        if (direction != this.direction.opposite()) {
            this.direction = direction;
        }
    }

    public boolean isAlive() {
        return alive;
    }

    public void kill() {
        alive = false;
    }

    public void grow() {
        pendingGrowth++;
    }

    public boolean hasSelfCollision() {
        Cell head = getHead();
        boolean first = true;
        for (Cell cell : body) {
            if (first) { 
                first = false;
                continue;
            }
            if (cell.equals(head)) {
                return true;
            }
        }
        return false;
    }

    public Set<Cell> getOccupiedCells() {
        return new HashSet<>(body);
    }

    public Cell move() {
        Cell newHead = getHead().move(direction);
        body.addFirst(newHead);
        if (pendingGrowth > 0) {
            pendingGrowth--;
        } else {
            body.removeLast();
        }
        return newHead;
    }

    public void shrink() {
        if (body.size() > 1) {
            body.removeLast();
        }
    }
}
