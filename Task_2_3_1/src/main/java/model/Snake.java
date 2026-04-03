package model;

import java.util.*;

public class Snake {
    private final Deque<Cell> body;
    private Direction direction;
    private boolean alive;
    private int pendingGrowth;
    private final Deque<Direction> directionQueue = new LinkedList<>();

    private final List<Cell> removedTails = new ArrayList<>();

    public Snake(Deque<Cell> body, Direction direction, boolean alive, int pendingGrowth) {
        this.body = body;
        this.direction = direction;
        this.alive = alive;
        this.pendingGrowth = pendingGrowth;
    }

    public List<Cell> getRemovedTails() {
        return removedTails;
    }

    public void clearRemovedTails() {
        removedTails.clear();
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

    public void setDirection(Direction newDirection) {
        Direction lastPlannedDirection = directionQueue.isEmpty() ? this.direction : directionQueue.getLast();
        if (newDirection != lastPlannedDirection && newDirection != lastPlannedDirection.opposite()) {
            if (directionQueue.size() < 2) {
                directionQueue.addLast(newDirection);
            }
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
        if (!directionQueue.isEmpty()) {
            this.direction = directionQueue.pollFirst();
        }
        Cell newHead = getHead().move(direction);
        body.addFirst(newHead);
        if (pendingGrowth > 0) {
            pendingGrowth--;
        } else {
            removedTails.add(body.removeLast());
        }
        return newHead;
    }

    public void shrink() {
        if (body.size() > 1) {
            removedTails.add(body.removeLast());
        }
    }
}
