package org.example;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BlockingQueue<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int limit;

    public BlockingQueue(int limit) {
        this.limit = limit;
    }

    public synchronized void put(T item) throws InterruptedException {
        while (queue.size() == limit) {
            wait();
        }
        queue.add(item);
        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        T item = queue.poll();
        notifyAll();
        return item;
    }

    public synchronized List<T> takeUpTo(int maxElements) throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        List<T> items = new ArrayList<>();
        while (!queue.isEmpty() && items.size() < maxElements) {
            items.add(queue.poll());
        }
        notifyAll();
        return items;
    }

    public synchronized boolean isEmpty() {
        return queue.isEmpty();
    }
}
