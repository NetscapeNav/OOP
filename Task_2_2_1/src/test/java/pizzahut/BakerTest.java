package pizzahut;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import static org.junit.jupiter.api.Assertions.*;

class BakerTest {

    @Test
    void bakerProcessesOrderThroughStates() throws InterruptedException {
        BlockingQueue<Order> bakerQueue = new BlockingQueue<>(10);
        BlockingQueue<Order> deliveryQueue = new BlockingQueue<>(10);

        Baker baker = new Baker(1, bakerQueue, deliveryQueue);
        Thread thread = new Thread(baker);
        thread.start();

        Order order = new Order(1, Order.State.PENDING);
        bakerQueue.put(order);

        Order delivered = deliveryQueue.take();
        assertSame(order, delivered);
        assertEquals(Order.State.STORAGED, delivered.getState());

        thread.interrupt();
        thread.join(2000);
    }

    @Test
    void bakerProcessesMultipleOrders() throws InterruptedException {
        BlockingQueue<Order> bakerQueue = new BlockingQueue<>(10);
        BlockingQueue<Order> deliveryQueue = new BlockingQueue<>(10);

        Baker baker = new Baker(1, bakerQueue, deliveryQueue);
        Thread thread = new Thread(baker);
        thread.start();

        int orderCount = 5;
        for (int i = 0; i < orderCount; i++) {
            bakerQueue.put(new Order(i, Order.State.PENDING));
        }

        for (int i = 0; i < orderCount; i++) {
            Order result = deliveryQueue.take();
            assertEquals(Order.State.STORAGED, result.getState());
        }

        thread.interrupt();
        thread.join(2000);
    }

    @Test
    void bakerStopsOnInterrupt() throws InterruptedException {
        BlockingQueue<Order> bakerQueue = new BlockingQueue<>(10);
        BlockingQueue<Order> deliveryQueue = new BlockingQueue<>(10);

        Baker baker = new Baker(1, bakerQueue, deliveryQueue);
        Thread thread = new Thread(baker);
        thread.start();

        Thread.sleep(200);
        thread.interrupt();
        thread.join(2000);

        assertFalse(thread.isAlive());
    }

    @Test
    void bakerSetsCorrectStates() throws InterruptedException {
        BlockingQueue<Order> bakerQueue = new BlockingQueue<>(10);
        BlockingQueue<Order> deliveryQueue = new BlockingQueue<>(10);

        Baker baker = new Baker(1, bakerQueue, deliveryQueue);
        Thread thread = new Thread(baker);
        thread.start();

        Order order = new Order(100, Order.State.PENDING);
        bakerQueue.put(order);

        Order processed = deliveryQueue.take();
        assertEquals(Order.State.STORAGED, processed.getState());
        assertEquals(100, processed.getId());

        thread.interrupt();
        thread.join(2000);
    }
}
