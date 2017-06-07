package CommonUtils;

/**
 * Created by ASPA on 05.06.2017.
 */
public class ConcurrentSlidingWindow<T> {
    private final CyclingFixedSizeDeque<T> deque;
    protected final Object lock = new Object();
    private int currentStart;
    private int currentEnd;
    private int size;

    public ConcurrentSlidingWindow(int size) {
        deque = new CyclingFixedSizeDeque<>(size);
        this.size = size;
    }

    protected T move() {
        synchronized (lock) {
            while (currentStart == currentEnd) {
                try {
                    lock.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            currentStart ++;
            T result = deque.remove();
            lock.notify();
            return result;
        }
    }

    public void read(T data) {
        synchronized (lock) {
            while (currentEnd - currentStart >= size) {
                try {
                    lock.wait();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            currentEnd++;
            deque.push(data);
            lock.notify();
        }
    }

    public T get(int i) {
        synchronized (lock) {
            while (i - currentStart >= deque.realSize())
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            return deque.get(i - currentStart);
        }
    }


    public int getCurrentStart() {
        return currentStart;
    }

    public int getCurrentEnd() {
        return currentEnd;
    }


}
