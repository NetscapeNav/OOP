package pizzahut;

import java.util.ArrayList;
import java.util.List;

public class Pizzeria {
    private final Config config;
    private final MyQueue<Order> bakerQueue;
    private final MyQueue<Order> deliveryQueue;

    private final List<Thread> bakers;
    private final List<Thread> delivers;

    public Pizzeria(Config config) {
        this.config = config;
        this.bakerQueue = new BlockingQueue<>(config.bakerQueueSize);
        this.deliveryQueue = new BlockingQueue<>(config.storage);
        this.bakers = new ArrayList<>();
        this.delivers = new ArrayList<>();
    }

    public void start() {
        Logger.info("Пиццерия открыта!");

        for (Config.BakerConfig bakerConfig : config.bakers) {
            Baker baker = new Baker(bakerConfig.speed, bakerQueue, deliveryQueue);
            Thread t = new Thread(baker);
            bakers.add(t);
            t.start();
        }

        for (Config.DeliveryConfig deliveryConfig : config.delivers) {
            DeliveryMan delivery = new DeliveryMan(deliveryConfig.time, deliveryConfig.storage, deliveryQueue);
            Thread t = new Thread(delivery);
            delivers.add(t);
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
        Logger.info("Пиццерия закрывается!");

        bakerQueue.close();

        for (Thread t : bakers) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        deliveryQueue.close();

        for (Thread t : delivers) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        Logger.info("Пиццерия закрылась");
    }

}
