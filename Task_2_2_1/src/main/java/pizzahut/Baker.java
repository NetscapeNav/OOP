package pizzahut;

public class Baker implements Runnable {
    private final int bakerSpeed;
    private final MyQueue<Order> bakerQueue;
    private final MyQueue<Order> deliveryQueue;

    public Baker(int bakerSpeed, MyQueue<Order> bakerQueue, MyQueue<Order> deliveryQueue) {
        this.bakerSpeed = bakerSpeed;
        this.bakerQueue = bakerQueue;
        this.deliveryQueue = deliveryQueue;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Order item = bakerQueue.take();

                if (item == null) {
                    break;
                }

                item.setState(Order.State.COOKING);
                Logger.printOrderState(item);

                Thread.sleep(bakerSpeed * 100L);

                item.setState(Order.State.COOKED);
                Logger.printOrderState(item);

                deliveryQueue.put(item);
                item.setState(Order.State.STORAGED);
                Logger.printOrderState(item);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
