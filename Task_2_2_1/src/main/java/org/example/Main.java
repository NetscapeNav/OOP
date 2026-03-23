package org.example;

import pizzahut.*;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class Main {
    public static void main(String[] args) {
        Config config;

        try (Reader reader = new FileReader("src/main/resources/setting.json")) {
            config = PizzaLoader.load(reader);
        } catch (IOException e) {
            System.out.println("Problem with config file: " + e.getMessage());
            return;
        }

        MyQueue<Order> bakerQueue = new BlockingQueue<>(config.bakerQueueSize);
        MyQueue<Order> deliveryQueue = new BlockingQueue<>(config.storage);
        Pizzeria pizzeria = new Pizzeria(config, bakerQueue, deliveryQueue);

        pizzeria.start();

        for (int i = 1; i <= 15; i++) {
            try {
                Logger.info("Поступил новый заказ: [" + i + "]");
                pizzeria.order(new Order(i));
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        pizzeria.stop();
    }
}