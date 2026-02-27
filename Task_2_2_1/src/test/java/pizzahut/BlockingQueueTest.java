package pizzahut;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class BlockingQueueTest {
    @Test
    void putAndTakeSingleElement() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(10);
        queue.put(42);
        assertEquals(42, queue.take());
    }

    @Test
    void fifoOrdering() throws InterruptedException {
        BlockingQueue<String> queue = new BlockingQueue<>(10);
        queue.put("first");
        queue.put("second");
        queue.put("third");
        assertEquals("first", queue.take());
        assertEquals("second", queue.take());
        assertEquals("third", queue.take());
    }

    @Test
    void isEmptyReturnsTrueForNewQueue() {
        BlockingQueue<Integer> queue = new BlockingQueue<>(5);
        assertTrue(queue.isEmpty());
    }

    @Test
    void isEmptyReturnsFalseAfterPut() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(5);
        queue.put(1);
        assertFalse(queue.isEmpty());
    }

    @Test
    void isEmptyReturnsTrueAfterTakeAll() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(5);
        queue.put(1);
        queue.take();
        assertTrue(queue.isEmpty());
    }

    @Test
    void takeUpToReturnsAvailableElements() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(10);
        queue.put(1);
        queue.put(2);
        queue.put(3);
        List<Integer> result = queue.takeUpTo(5);
        assertEquals(List.of(1, 2, 3), result);
        assertTrue(queue.isEmpty());
    }

    @Test
    void takeUpToRespectsMaxElements() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(10);
        queue.put(1);
        queue.put(2);
        queue.put(3);
        queue.put(4);
        queue.put(5);
        List<Integer> result = queue.takeUpTo(3);
        assertEquals(List.of(1, 2, 3), result);
        assertFalse(queue.isEmpty());
    }

    @Test
    void putBlocksWhenFull() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(1);
        queue.put(1);

        AtomicBoolean putCompleted = new AtomicBoolean(false);
        Thread producer = new Thread(() -> {
            try {
                queue.put(2);
                putCompleted.set(true);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        producer.start();

        Thread.sleep(200);
        assertFalse(putCompleted.get(), "put() should block when queue is full");

        queue.take();
        producer.join(2000);
        assertTrue(putCompleted.get(), "put() should complete after space is freed");
    }

    @Test
    void takeBlocksWhenEmpty() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(10);
        AtomicReference<Integer> result = new AtomicReference<>();

        Thread consumer = new Thread(() -> {
            try {
                result.set(queue.take());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();

        Thread.sleep(200);
        assertNull(result.get(), "take() should block when queue is empty");

        queue.put(99);
        consumer.join(2000);
        assertEquals(99, result.get());
    }

    @Test
    void takeUpToBlocksWhenEmpty() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(10);
        AtomicReference<List<Integer>> result = new AtomicReference<>();

        Thread consumer = new Thread(() -> {
            try {
                result.set(queue.takeUpTo(3));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        consumer.start();

        Thread.sleep(200);
        assertNull(result.get(), "takeUpTo() should block when queue is empty");

        queue.put(10);
        consumer.join(2000);
        assertEquals(List.of(10), result.get());
    }

    @Test
    void concurrentProducersAndConsumers() throws InterruptedException {
        BlockingQueue<Integer> queue = new BlockingQueue<>(5);
        int totalItems = 100;
        CountDownLatch latch = new CountDownLatch(totalItems);
        java.util.concurrent.ConcurrentLinkedQueue<Integer> consumed =
            new java.util.concurrent.ConcurrentLinkedQueue<>();

        Thread[] producers = new Thread[3];
        for (int p = 0; p < producers.length; p++) {
            int startVal = p * 34;
            int count = (p < 2) ? 34 : 32; // 34 + 34 + 32 = 100
            producers[p] = new Thread(() -> {
                try {
                    for (int i = 0; i < count; i++) {
                        queue.put(startVal + i);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        Thread[] consumers = new Thread[2];
        for (int c = 0; c < consumers.length; c++) {
            consumers[c] = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        Integer item = queue.take();
                        consumed.add(item);
                        latch.countDown();
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        for (Thread t : consumers) t.start();
        for (Thread t : producers) t.start();

        latch.await();

        for (Thread t : producers) t.join(2000);
        for (Thread t : consumers) t.interrupt();
        for (Thread t : consumers) t.join(2000);

        assertEquals(totalItems, consumed.size());
    }
}
