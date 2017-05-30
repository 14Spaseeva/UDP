package Client;

import CommonUtils.Channel;
import Exceptions.SlidingWindowControllerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Created by ASPA on 05.05.2017.
 */

/**
 * реальзует алгоритм sliding window
 */
public class SlidingWindowController {

    private static Logger log = LoggerFactory.getLogger("clientSlidingWindowconroller");

    //как только получили инфу что пакет принят двигаемся дальше с помощью receiver
    //пока без таймера (пакеты всегда доходят и сервер всегда отсылает ответ)
    private Channel<Object> channel;
    private ArrayList<byte[]> window; //куски файла
    private ArrayList<Integer> timer; //время, оставшееся до повторной отправки
    private int timeout;
    private volatile int leftWindowIndex; //границы окна
    private volatile int rightWindowIndex;
    private volatile boolean isFreeToPull;
    private volatile int maxSize; //кол-во частей файла


    private final Object lock = new Object();
    private final Object lockTimer = new Object();


    public SlidingWindowController(int capacity, Channel channel, int timeout) {
        this.channel = channel;
        window = new ArrayList<>();
        timer = new ArrayList<>();
        this.timeout = timeout;

        leftWindowIndex = 0;
        rightWindowIndex = capacity - 1;
        isFreeToPull = true;
        maxSize = -1;
    }

    public void setMaxSize(int size) throws SlidingWindowControllerException {
        if (size >= 0)
            this.maxSize = size;
        else throw new SlidingWindowControllerException("trial to set maxsize <0");
    }

    public byte[] getBytes() {
        return (byte[]) channel.get();
    }

    /**
     * кладет кусок файла в окно и канал, окно не заполнено, иначе ждет
     * @param buffer
     */
    public void push(byte[] buffer) {

        synchronized (lock) {
            while (!isFreeToPull) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    log.error("cant wait", e);
                }
            }
            window.add(buffer);
            isFreeToPull = window.size() != rightWindowIndex + 1;
            synchronized (lockTimer) {
                timer.add(timeout);
            }

            channel.put(buffer);
        }
    }

    /**
     * кладет в канал кусок файла с заданным индексом
     * @param index
     */
    public void doResend(int index)//    кладет в channel кусок файла с номером index;
    {
        byte[] buffer;
        synchronized (lock) {
            buffer = window.get(index);
        }
        if (buffer != null)
            channel.put(buffer);
    }

    /**
     * помечает файл как доставленный, двигает границы окна
     * @param index
     */
    public void setReceived(int index) {
        synchronized (lock) {
            window.set(index, null);
        }

        synchronized (lockTimer) {
            timer.set(index, -1);
            int left = this.leftWindowIndex;
            int size = timer.size();
            for (int i = left; i < size; ++i) {
                if (timer.get(i) == -1) {
                    this.leftWindowIndex++;
                    if (maxSize != -1)
                        rightWindowIndex++;
                    else
                        rightWindowIndex = maxSize;
                    isFreeToPull = true;
                } else break;
            }
        }
        if (leftWindowIndex > rightWindowIndex) {
            System.exit(0);
        }

        synchronized (lock) {
            if (isFreeToPull)
                lock.notify();
        }
    }

    /**
     * обновляет таймер
     */
    public void doTimerIteration() { //обновляет таймер
        synchronized (lockTimer) {
            int left = leftWindowIndex;
            int size = timer.size();
            for (int i = left; i < size; ++i) {
                int t = timer.get(i);
                if (t == -1)
                    continue;
                if (t == 1) {
                    timer.set(i, timeout);
                    doResend(i);
                } else
                    timer.set(i, t - 1);
            }
        }
    }

}