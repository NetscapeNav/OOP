package pizzahut;

public class Logger {
    public static synchronized void printOrderState(Order order) {
        System.out.println("[" + order.getId() + "] " + order.getState());
    }

    public static synchronized void info(String message) {
        System.out.println(message);
    }
}
