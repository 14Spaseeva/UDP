package Client;

import CommonUtils.Channel;
import CommonUtils.SlidingWindow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.IIOException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by ASPA on 05.05.2017.
 */
public class FileReader {
    private static Logger log = LoggerFactory.getLogger("fileReader");

    Thread thread;
    private volatile boolean flag;
    private static final Object lock = new Object();


    public FileReader(Callback callback, String filename, byte[] buf, Channel<InitPackage> databuf) {
        new Thread(() -> {
            //TODO  в Receiver , Sender тоже
            //читает данные из фпайлв и склыдывает их в буфер
            synchronized (lock) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(filename);
                    byte[] buffer = buf;
                    Channel<InitPackage> dataBuffer = databuf;
                    int len = buffer.length;
                    while (true) {
                        fileInputStream.read(buffer, len, fileInputStream.available());

                    }
                } catch (IOException e) {
                    log.error("error of reading file, ", e);
                }
            }
        }).start();

    }

     interface Callback {
            void  onRead(bytes[] ...)
    }

}



