package pizzahut;

public class Baker implements Runnable {
    private final int bakerSpeed;
    private final BlockingQueue<Order> bakerQueue;
    private final BlockingQueue<Order> deliveryQueue;

    public Baker(int bakerSpeed, BlockingQueue<Order> bakerQueue, BlockingQueue<Order> deliveryQueue) {
        this.bakerSpeed = bakerSpeed;
        this.bakerQueue = bakerQueue;
        this.deliveryQueue = deliveryQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Order item = bakerQueue.take();
                item.setState(Order.State.COOKING);

                Thread.sleep(bakerSpeed * 100L);

                item.setState(Order.State.COOKED);

                deliveryQueue.put(item);
                item.setState(Order.State.STORAGED);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
