package pizzahut;

import java.util.List;

public interface MyQueue<T> {
    void put(T item) throws InterruptedException;
    T take() throws InterruptedException;
    List<T> takeUpTo(int maxElements) throws InterruptedException;
    boolean isEmpty();
    void close();
}
