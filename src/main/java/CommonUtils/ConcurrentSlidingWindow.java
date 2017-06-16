package CommonUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by ASPA on 05.06.2017.
 */
public class ConcurrentSlidingWindow { //
    private final CyclingFixedSizeDeque deque;
    protected final Object lock = new Object();
    private int currentStart;
    private int currentEnd;
    private int size;

    public ConcurrentSlidingWindow(int size) {
        deque = new CyclingFixedSizeDeque(size);
        this.size = size;
    }

    public PartOfFile move() {
        synchronized (lock) {
            while (currentStart == currentEnd) {
                try {
                    lock.wait(300);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
            }
            currentStart ++;
            PartOfFile result = deque.remove();
            lock.notify();
            return result;
        }
    }

    public void read(PartOfFile data) {
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

    public PartOfFile  get(int i) {
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

    public void setSendingTime(int number, long sending) {
        synchronized (lock) {
            if (number >= getCurrentStart()) {
                try {
                    get(number).timeOfSending = sending;
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void setConfirm(int number) {
        synchronized (lock) {
            if (number >= getCurrentStart())
                get(number).confirm = true;
        }
    }

    public List<PartOfFile> moveWindow() {
        synchronized (lock) {
            LinkedList<PartOfFile> result = new LinkedList<>();
            while (get(getCurrentStart()).confirm) {
                result.add(move());
            }
            return result;
        }
    }

    public Stream<PartOfFile> getNotConfirmedParts(long timeOutInMilliseconds) {
        synchronized (lock) {
            long now = System.currentTimeMillis();
            return IntStream.range(getCurrentStart(), getCurrentEnd())
                    .mapToObj(this::get)
                    .filter(timedPartOfFile -> timedPartOfFile != null && !timedPartOfFile.confirm && timedPartOfFile.timeOfSending != 0 && now - timedPartOfFile.timeOfSending > timeOutInMilliseconds);
        }
    }

}