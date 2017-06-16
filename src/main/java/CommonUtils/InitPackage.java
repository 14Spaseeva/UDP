package CommonUtils;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by ASPA on 05.05.2017.
 */
public class InitPackage implements Serializable {
    public long fileSize;
    public String fileName;
    public long totalPackageCount;


    public InitPackage(long fileSize, String fileName, long totalPackageCount) {
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.totalPackageCount = totalPackageCount;
    }

    public static InitPackage fromBytes(byte[] bytes) throws IOException {
        return Util.convertFromBytes(bytes);
    }

    public static byte[] toBytes(InitPackage initPackage) throws IOException {
        return Util.toBytes(initPackage);
    }

    public byte[] toBytes() throws IOException {
        return toBytes(this);
    }
}
