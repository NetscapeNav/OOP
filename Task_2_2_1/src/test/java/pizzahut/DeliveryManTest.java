package pizzahut;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryManTest {
    @Test
    void deliveryManDeliversSingleOrder() throws InterruptedException {
        BlockingQueue<Order> deliveryQueue = new BlockingQueue<>(10);

        DeliveryMan deliveryMan = new DeliveryMan(1, 3, deliveryQueue);
        Thread thread = new Thread(deliveryMan);
        thread.start();

        Order order = new Order(1, Order.State.STORAGED);
        deliveryQueue.put(order);

        Thread.sleep(500);
        assertEquals(Order.State.DELIVERED, order.getState());

        thread.interrupt();
        thread.join(2000);
    }

    @Test
    void deliveryManDeliversMultipleOrders() throws InterruptedException {
        BlockingQueue<Order> deliveryQueue = new BlockingQueue<>(10);

        DeliveryMan deliveryMan = new DeliveryMan(1, 5, deliveryQueue);
        Thread thread = new Thread(deliveryMan);
        thread.start();

        Order[] orders = new Order[3];
        for (int i = 0; i < 3; i++) {
            orders[i] = new Order(i, Order.State.STORAGED);
            deliveryQueue.put(orders[i]);
        }

        Thread.sleep(500);
        for (Order order : orders) {
            assertEquals(Order.State.DELIVERED, order.getState());
        }

        thread.interrupt();
        thread.join(2000);
    }

    @Test
    void deliveryManRespectsStorageLimit() throws InterruptedException {
        BlockingQueue<Order> deliveryQueue = new BlockingQueue<>(10);

        DeliveryMan deliveryMan = new DeliveryMan(1, 2, deliveryQueue);
        Thread thread = new Thread(deliveryMan);
        thread.start();

        Order[] orders = new Order[4];
        for (int i = 0; i < 4; i++) {
            orders[i] = new Order(i, Order.State.STORAGED);
            deliveryQueue.put(orders[i]);
        }

        Thread.sleep(1000);
        for (Order order : orders) {
            assertEquals(Order.State.DELIVERED, order.getState());
        }

        thread.interrupt();
        thread.join(2000);
    }

    @Test
    void deliveryManStopsOnInterrupt() throws InterruptedException {
        BlockingQueue<Order> deliveryQueue = new BlockingQueue<>(10);

        DeliveryMan deliveryMan = new DeliveryMan(1, 3, deliveryQueue);
        Thread thread = new Thread(deliveryMan);
        thread.start();

        Thread.sleep(200);
        thread.interrupt();
        thread.join(2000);

        assertFalse(thread.isAlive());
    }

    @Test
    void deliveryManSetsDeliveringStateDuringDelivery() throws InterruptedException {
        BlockingQueue<Order> deliveryQueue = new BlockingQueue<>(10);

        DeliveryMan deliveryMan = new DeliveryMan(50, 3, deliveryQueue);
        Thread thread = new Thread(deliveryMan);
        thread.start();

        Order order = new Order(1, Order.State.STORAGED);
        deliveryQueue.put(order);

        Thread.sleep(200);
        assertEquals(Order.State.DELIVERING, order.getState());

        Thread.sleep(1000);
        assertEquals(Order.State.DELIVERED, order.getState());

        thread.interrupt();
        thread.join(2000);
    }

    @Test
    void interruptDuringSleepDeliversAndStops() throws InterruptedException {
        BlockingQueue<Order> deliveryQueue = new BlockingQueue<>(10);
        DeliveryMan deliveryMan = new DeliveryMan(500, 5, deliveryQueue);
        Thread thread = new Thread(deliveryMan);
        thread.start();

        Order order = new Order(99, Order.State.STORAGED);
        deliveryQueue.put(order);

        while (order.getState() != Order.State.DELIVERING) {
            Thread.sleep(20);
        }

        thread.interrupt();
        thread.join(3000);

        assertEquals(Order.State.DELIVERED, order.getState());
        assertFalse(thread.isAlive());
    }
}
