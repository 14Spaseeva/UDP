package CommonUtils;

import java.util.Arrays;

/**
 * Created by ASPA on 05.06.2017.
 */
class CyclingFixedSizeDeque{
    private int head;
    private int tail;
    private final Object[] data;

    public CyclingFixedSizeDeque(int length) {
        this.data = new Object[length];
    }

    public void push(PartOfFile elem) {
        data[(tail++) % data.length] = elem;
    }

    public PartOfFile remove() {
        if (head == tail)
            throw new IllegalStateException("head = tail");
        int index = (head++) % data.length;
        final PartOfFile result = (PartOfFile) data[index];
        data[index] = null;
        return result;
    }

    public PartOfFile get(int i) {
        if (i >= data.length)
            throw new IllegalArgumentException("Element is not in window");
        return (PartOfFile) data[(head + i) % data.length];
    }

    public int realSize() {
        int realSize = 0;
        for (Object aData : data) {
            if (aData != null)
                realSize++;
        }
        return realSize;
    }

    public int size() {
        return data.length;
    }

    public PartOfFile getHead() {
        return (PartOfFile) data[head % size()];
    }

    public PartOfFile getTail() {
        return (PartOfFile) data[tail % size()];
    }
}