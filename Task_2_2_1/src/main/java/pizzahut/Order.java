package pizzahut;

public class Order {
    private final int id;
    private State status;

    public Order(int id, State status) {
        this.id = id;
        this.status = status;
    }

    public enum State {
        PENDING, COOKING, COOKED, STORAGED, DELIVERING, DELIVERED
    }

    public void setState(State state) {
        status = state;
        System.out.println("[" + id + "] " + status);
    }

    public State getState() {
        return status;
    }

    public int getId() {
        return id;
    }
}
