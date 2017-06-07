package CommonUtils;

/**
 * Created by ASPA on 07.06.2017.
 */
public class TimedPartOfFile extends PartOfFile {

    private transient volatile long timeOfSending;
    private transient volatile boolean confirm;

    public TimedPartOfFile(byte[] data, int number) {
        super(data, number);
    }

    public boolean getConfirm() {
        return confirm;
    }

    public long gettimeOfSending() {
        return timeOfSending;
    }

    public void settimeOfSending(long timeOfSending) {
        this.timeOfSending = timeOfSending;
    }

    public void setConfirm() {
        this.confirm = true;
    }
}
