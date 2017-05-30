package CommonUtils;

/**
 * Created by ASPA on 05.05.2017.
 */
public class PartOfFile {
    byte[] buffer;
    int packageNumber;
    boolean isDelivered;
    //+время отправки


    public boolean isDelivered() {
        return isDelivered;
    }

    public void setDelivered(boolean delivered) {
        isDelivered = delivered;
    }

    void setBuffer(byte[] b) {
        buffer = b;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    void setPackageNumber(int n) {
        packageNumber = n;
    }

    public int getPackageNumber() {
        return packageNumber;
    }


}
