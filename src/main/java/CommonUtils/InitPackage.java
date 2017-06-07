package CommonUtils;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by ASPA on 05.05.2017.
 */
public class InitPackage implements Serializable {
    private static final long serialVersionUID = 1L;

    private long fileSize;
    private String fileName;
    public long totalPackageCount;

    public InitPackage(long fileSize, String fileName, long packageCount) {
        //TODO переводит в байты и обратно
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.totalPackageCount = packageCount;
    }

    public static InitPackage fromBytes(byte[] bytes) throws IOException {
        return Util.convertFromBytes(bytes);
    }

    private static byte[] toBytes(InitPackage initPackage) throws IOException {
        return Util.toBytes(initPackage);
    }

    public byte[] toBytes() throws IOException {
        return toBytes(this);
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileSize() {
        return fileSize;
    }
    /*byte[] toBytes() throws IOException {
        byte[] array = Files.readAllBytes(Paths.get(fileName)); //перевели весь файл в массив байт
        return array;
    }

    void FromBytes() {

    }*/
}
