package pizzahut;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void constructorSetsIdAndState() {
        Order order = new Order(1, Order.State.PENDING);
        assertEquals(1, order.getId());
        assertEquals(Order.State.PENDING, order.getState());
    }

    @Test
    void setStateChangesState() {
        Order order = new Order(42, Order.State.PENDING);
        order.setState(Order.State.COOKING);
        assertEquals(Order.State.COOKING, order.getState());
    }

    @Test
    void fullLifecycleTransitions() {
        Order order = new Order(7, Order.State.PENDING);
        Order.State[] lifecycle = {
            Order.State.COOKING,
            Order.State.COOKED,
            Order.State.STORAGED,
            Order.State.DELIVERING,
            Order.State.DELIVERED
        };
        for (Order.State state : lifecycle) {
            order.setState(state);
            assertEquals(state, order.getState());
        }
    }

    @Test
    void getIdReturnsCorrectValue() {
        Order o1 = new Order(0, Order.State.PENDING);
        Order o2 = new Order(999, Order.State.PENDING);
        assertEquals(0, o1.getId());
        assertEquals(999, o2.getId());
    }

    @Test
    void stateEnumHasAllValues() {
        Order.State[] states = Order.State.values();
        assertEquals(6, states.length);
        assertEquals(Order.State.PENDING, Order.State.valueOf("PENDING"));
        assertEquals(Order.State.COOKING, Order.State.valueOf("COOKING"));
        assertEquals(Order.State.COOKED, Order.State.valueOf("COOKED"));
        assertEquals(Order.State.STORAGED, Order.State.valueOf("STORAGED"));
        assertEquals(Order.State.DELIVERING, Order.State.valueOf("DELIVERING"));
        assertEquals(Order.State.DELIVERED, Order.State.valueOf("DELIVERED"));
    }
}
