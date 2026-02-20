package org.example;

import java.util.ArrayList;
import java.util.List;

public class Pizzeria {
    private final Config config;
    private final BlockingQueue<Order> bakerQueue;
    private final BlockingQueue<Order> deliveryQueue;
    private final List<Thread> workerThreads;

    public Pizzeria(Config config) {
        this.config = config;
        this.bakerQueue = new BlockingQueue<>(1000);
        this.deliveryQueue = new BlockingQueue<>(config.storage);
        this.workerThreads = new ArrayList<>();
    }

    public void start() {
        System.out.println("Пиццерия открыта!");

        for (Config.BakerConfig bakerConfig : config.bakers) {
            Baker baker = new Baker(bakerConfig.speed, bakerQueue, deliveryQueue);
            Thread t = new Thread(baker);
            workerThreads.add(t);
            t.start();
        }

        for (Config.DeliveryConfig deliveryConfig : config.delivers) {
            DeliveryMan delivery = new DeliveryMan(deliveryConfig.time, deliveryConfig.storage, deliveryQueue);
            Thread t = new Thread(delivery);
            workerThreads.add(t);
            t.start();
        }
    }

    public void order(Order order) {
        try {
            bakerQueue.put(order);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        System.out.println("Пиццерия закрывается!");
        try {
            while (!bakerQueue.isEmpty()) {
                Thread.sleep(500);
            }
            while (!deliveryQueue.isEmpty()) {
                Thread.sleep(500);
            }
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (Thread t : workerThreads) {
            t.interrupt();
        }

        for (Thread t : workerThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Пиццерия закрылась");
    }

}
