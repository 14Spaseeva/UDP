package Client;

import java.io.Serializable;

/**
 * Created by ASPA on 05.05.2017.
 */
public class InitPackage implements Serializable{
    private long fileSize;
    private String fileName;
    private long packageCount;

    InitPackage(long fileSize, String fileName, long packageCount){
        //TODO переводит в байты и обратно
        this.fileSize=fileSize;
    }
}
