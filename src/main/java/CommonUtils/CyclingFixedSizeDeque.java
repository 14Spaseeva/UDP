package CommonUtils;

import java.util.Arrays;

/**
 * Created by ASPA on 05.06.2017.
 */
public class CyclingFixedSizeDeque <T>{
    private int head;
    private int tail;
    private final Object[] data;

    CyclingFixedSizeDeque(int length) {
        this.data = new Object[length];
    }

    void push(T elem) {
        data [(tail++) % data.length] = elem;
    }

    T remove() {
        if (head == tail)
            throw new IllegalStateException("head = tail");
        int index = (head++) % data.length;
        final T result = (T) data[index];
        data[index] = null;
        return result;
    }

    public T get(int i) {
        if (i >= data.length)
            throw new IllegalArgumentException("i>=data.length!");
        return (T) data[(head + i) % data.length];
    }

    int realSize() {
        int realSize = 0;
        for (Object aData : data) {
            if (aData != null)
                realSize++;
        }
        return realSize;
    }

    private int size() {
        return data.length;
    }



}
