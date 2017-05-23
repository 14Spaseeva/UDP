package CommonUtils;

/**
 * Created by ASPA on 05.05.2017.
 */
public class PartOfFile {
    byte[] bytes;
    int packageNumber;
    boolean isDelivered;
    //+время отправки

    void setBytes(byte[] b) {
        bytes = b;
    }

    void setPackageNumber(int n) {
        packageNumber = n;
    }
}
