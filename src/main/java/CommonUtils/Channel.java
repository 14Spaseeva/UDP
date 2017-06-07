package CommonUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;


/**
 * @param <T>
 *       здесь хранится максимально возможное кол-во сессий
 *       */

public class Channel<T> {

    private static Logger log = LoggerFactory.getLogger("channel");

    private final int maxSize;
    private final LinkedList<T> queue; //очередь незапущенных клиентов, пришедших из хоста /// нам он не нуженб
    private final Object lock = new Object();

    public Channel(int num) {

        maxSize = num;
        queue = new LinkedList<>();
    }

    /**
    , когда поток пытается положить элементы в полную очередь,
    он ставится в ожидание до тех пор, пока какой-нибудь другой поток
    не возьмет элементы их очереди и таким образом не освободит место в ней.
     */
    public void put(T obj) {


        synchronized (lock) {
            while (queue.size() >= maxSize) {
                try {
                    log.info("New Session is waiting ");
                    lock.wait();
                } catch (InterruptedException e) {
                    log.error("Oops: Channel put ! {}", e);
                    return;
                }
            }
            queue.addLast(obj);
            lock.notifyAll(); //пробуждает потоки, которые поставлены в ожидание при получении элементов из очереди
        }
    }


    /**
    Когда поток пытается получить элементы из пустой очереди,
    он ставится в ожидание до тех пор, пока какой-нибудь другой
    поток не положит элементы в очередь.
     */
    public T get() {
        synchronized (lock) {
            while (queue.isEmpty()) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    log.error("Ошибка ожидания");
                }
            }
            lock.notifyAll();
            return queue.removeFirst();
        }
    }


}
