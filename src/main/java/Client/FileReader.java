package Client;

import Exceptions.SlidingWindowControllerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by ASPA on 05.05.2017.
 */
public class FileReader implements Runnable {
    private static Logger log = LoggerFactory.getLogger("fileReader");
    private final int packageSize;
    private final SlidingWindowController slidingWindowController;

    private FileInputStream fileInputStream;
    private String fileName;

    Thread thread;


    public FileReader(FileInputStream fileInputStream, String fileName, int blockSize, SlidingWindowController slidingWindowController) {
        this.fileInputStream = fileInputStream;
        this.packageSize = blockSize;
        this.slidingWindowController = slidingWindowController;
        this.fileName = fileName;
    }

    void createPartsOfFile(InitPackage initPackage) {

    }

    @Override
    public void run() {

        int read = 0;
        boolean isNotCompleteRead = true;
        //читает данные из фпайлв и склыдывает их в буфер
        byte buffer[] = new byte[fileName.length() + 4];
        byte tmp[] = fileName.getBytes();
        System.arraycopy(tmp, 0, buffer, 4, tmp.length);
        slidingWindowController.push(buffer);
        int id = 1;
        while (isNotCompleteRead) {
            try {

                byte[] index = ByteBuffer.allocate(4).putInt(id).array(); //IntToByte заголовка
                buffer = new byte[packageSize + 4];
                read = fileInputStream.read(buffer, 4, packageSize);
                if (read < packageSize) {
                    if (read < 0) read = 0;
                    byte[] lastBuffer = new byte[read + 4];
                    System.arraycopy(buffer, 4, lastBuffer, 4, read);
                    System.arraycopy(index, 0, lastBuffer, 0, 4);
                    isNotCompleteRead = false;

                    if (id == 1) {
                        byte[] zero_buf = ByteBuffer.allocate(4).putInt(2).array();
                        slidingWindowController.push(lastBuffer);
                        slidingWindowController.push(zero_buf);
                        slidingWindowController.setMaxSize(2);

                    } else {
                        slidingWindowController.push(lastBuffer);
                        slidingWindowController.setMaxSize(id);
                    }


                } else {
                    System.arraycopy(index, 0, buffer, 0, 4);
                    slidingWindowController.push(buffer);
                }
                ++id;
            } catch (IOException e) {
                log.error("not connected");
            } catch (SlidingWindowControllerException e) {
                log.error("", e);
            }
        }
        try {
            fileInputStream.close();
        } catch (IOException e) {
            log.error("impossible to close fileinputstream");
        }
    }
}



