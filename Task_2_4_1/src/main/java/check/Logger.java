package check;

public class Logger {
    public static synchronized void info(String message) {
        System.out.println(message);
    }

    public static synchronized void error(String message, Exception e) {
        System.out.println("[ERROR] " + message);
        if (e != null) {
            e.printStackTrace();
        }
    }
}
