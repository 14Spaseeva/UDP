package Client;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by ASPA on 05.05.2017.
 */
public class InitPackage implements Serializable {

    private long fileSize;
    private String fileName;
    private long packageCount;

    InitPackage(long fileSize, String fileName, long packageCount) {
        //TODO переводит в байты и обратно
        this.fileSize = fileSize;
        this.fileName = fileName;
        this.packageCount = packageCount;
    }

    byte[] toBytes() throws IOException {
        byte[] array = Files.readAllBytes(Paths.get(fileName)); //перевели весь файл в массив байт
        return array;
    }

    void FromBytes() {

    }
}
