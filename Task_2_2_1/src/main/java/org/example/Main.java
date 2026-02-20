package org.example;

import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputFilter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        Config config;

        try (Reader reader = new FileReader("src/main/resources/setting.json")) {
            config = PizzaLoader.load(reader);
        } catch (IOException e) {
            System.out.println("Problem with config file: " + e.getMessage());
            return;
        }

        BlockingQueue<Order> orderQueue = new BlockingQueue<>(1000);
        BlockingQueue<Order> deliveryQueue = new BlockingQueue<>(config.storage);

        List<Thread> workerThreads = new ArrayList<>();

        for (Config.BakerConfig bakerConfig : config.bakers) {
            Baker baker = new Baker(bakerConfig.speed, orderQueue, deliveryQueue);
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

        System.out.println("Пиццерия открыта!");
        for (int i = 1; i <= 15; i++) {
            try {
                Order order = new Order(i, Order.State.PENDING);
                System.out.println("Поступил новый заказ: [" + i + "]");
                orderQueue.put(order);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Пиццерия закрывается!");
        try {
            while (!orderQueue.isEmpty()) {
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