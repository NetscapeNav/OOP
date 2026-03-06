package pizzahut;

public class Order {
    private final int id;
    private State status;

    public Order(int id) {
        this.id = id;
        this.status = State.PENDING;
    }

    public enum State {
        PENDING, COOKING, COOKED, STORAGED, DELIVERING, DELIVERED
    }

    public void setState(State state) {
        status = state;
    }

    public State getState() {
        return status;
    }

    public int getId() {
        return id;
    }
}
