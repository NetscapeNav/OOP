package org.example;

import pizzahut.Config;
import pizzahut.Order;
import pizzahut.PizzaLoader;
import pizzahut.Pizzeria;

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

        Pizzeria pizzeria = new Pizzeria(config);

        pizzeria.start();

        for (int i = 1; i <= 15; i++) {
            try {
                System.out.println("Поступил новый заказ: [" + i + "]");
                pizzeria.order(new Order(i, Order.State.PENDING));
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        pizzeria.stop();
    }
}