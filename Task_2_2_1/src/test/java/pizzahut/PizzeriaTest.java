package pizzahut;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PizzeriaTest {

    private Config makeConfig(int storage, int[] bakerSpeeds, int[][] deliveryConfigs) {
        Config config = new Config();
        config.storage = storage;
        config.bakers = new ArrayList<>();
        for (int speed : bakerSpeeds) {
            Config.BakerConfig b = new Config.BakerConfig();
            b.speed = speed;
            config.bakers.add(b);
        }
        config.delivers = new ArrayList<>();
        for (int[] dc : deliveryConfigs) {
            Config.DeliveryConfig d = new Config.DeliveryConfig();
            d.storage = dc[0];
            d.time = dc[1];
            config.delivers.add(d);
        }
        return config;
    }

    @Test
    void pizzeriaProcessesSingleOrder() {
        Config config = makeConfig(10, new int[]{1}, new int[][]{{3, 1}});
        Pizzeria pizzeria = new Pizzeria(config);
        pizzeria.start();

        Order order = new Order(1, Order.State.PENDING);
        pizzeria.order(order);

        try { Thread.sleep(2000); } catch (InterruptedException ignored) {}

        assertEquals(Order.State.DELIVERED, order.getState());
        pizzeria.stop();
    }

    @Test
    void pizzeriaProcessesMultipleOrders() {
        Config config = makeConfig(10,
            new int[]{1, 1},
            new int[][]{{5, 1}, {5, 1}});
        Pizzeria pizzeria = new Pizzeria(config);
        pizzeria.start();

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Order order = new Order(i, Order.State.PENDING);
            orders.add(order);
            pizzeria.order(order);
        }

        pizzeria.stop();

        for (Order order : orders) {
            assertEquals(Order.State.DELIVERED, order.getState(),
                "Order " + order.getId() + " should be DELIVERED");
        }
    }

    @Test
    void pizzeriaStartsAndStopsCleanly() {
        Config config = makeConfig(5, new int[]{1}, new int[][]{{2, 1}});
        Pizzeria pizzeria = new Pizzeria(config);
        pizzeria.start();

        pizzeria.stop();
    }

    @Test
    void pizzeriaWithMultipleBakers() {
        Config config = makeConfig(10,
            new int[]{1, 2, 3},
            new int[][]{{5, 1}});
        Pizzeria pizzeria = new Pizzeria(config);
        pizzeria.start();

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Order order = new Order(i, Order.State.PENDING);
            orders.add(order);
            pizzeria.order(order);
        }

        pizzeria.stop();

        for (Order order : orders) {
            assertEquals(Order.State.DELIVERED, order.getState());
        }
    }

    @Test
    void pizzeriaWithMultipleDeliveryMen() {
        Config config = makeConfig(10,
            new int[]{1},
            new int[][]{{2, 1}, {2, 1}, {2, 1}});
        Pizzeria pizzeria = new Pizzeria(config);
        pizzeria.start();

        List<Order> orders = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Order order = new Order(i, Order.State.PENDING);
            orders.add(order);
            pizzeria.order(order);
        }

        pizzeria.stop();

        for (Order order : orders) {
            assertEquals(Order.State.DELIVERED, order.getState());
        }
    }
}
