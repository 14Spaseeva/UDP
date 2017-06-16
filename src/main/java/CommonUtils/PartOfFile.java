package CommonUtils;

/**
 * Created by ASPA on 05.05.2017.
 */
public class PartOfFile {
    public final byte[] data;
    public final int number;

    public transient volatile long timeOfSending;
    public transient volatile boolean confirm;

    public PartOfFile(byte[] data, int number) {
        this.data = data;
        this.number = number;
    }

    public byte[] getData() {
        return data;
    }

    public int getNumber() {
        return number;
    }

    public long getTimeOfSending() {
        return timeOfSending;
    }

    public void setTimeOfSending(long timeOfSending) {
        this.timeOfSending = timeOfSending;
    }

    public boolean isConfirm() {
        return confirm;
    }

    public void setConfirm(boolean confirm) {
        this.confirm = confirm;
    }


}
