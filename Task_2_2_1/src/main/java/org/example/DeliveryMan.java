package org.example;

import java.util.List;

import static java.lang.Thread.sleep;

public class DeliveryMan implements Runnable {
    private final int deliverySpeed;
    private final int deliveryStorage;
    private final BlockingQueue<Order> deliveryQueue;

    public DeliveryMan(int bakerSpeed, int deliveryStorage, BlockingQueue<Order> deliveryQueue) {
        this.deliverySpeed = bakerSpeed;
        this.deliveryStorage = deliveryStorage;
        this.deliveryQueue = deliveryQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                List<Order> items = deliveryQueue.takeUpTo(deliveryStorage);
                for (Order item : items) {
                    item.setState(Order.State.DELIVERING);
                }
                try {
                    Thread.sleep(deliverySpeed * 10L);
                } catch (InterruptedException e) {
                    for (Order item : items) {
                        item.setState(Order.State.DELIVERED);
                    }
                    Thread.currentThread().interrupt();
                    return;
                }
                for (Order item : items) {
                    item.setState(Order.State.DELIVERED);
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
