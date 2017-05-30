package Server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ASPA on 05.05.2017.
 */
public class FileWriter implements Runnable{
    private static Logger log = LoggerFactory.getLogger("fileWriter");

    private final String filePath;
    private FileOutputStream fileOutputStream;
    private final ReceiverSlidingWindow receiverSlidingWindow;
    private boolean status;
    private int packetSize;


        public FileWriter(String filePath, ReceiverSlidingWindow receiverSlidingWindow) {
            this.receiverSlidingWindow = receiverSlidingWindow;
            this.filePath = filePath;
            packetSize = 0;
            status = false;
        }


    @Override
    public void run() {
        String fileName = receiverSlidingWindow.getFileName();
        try {
            fileOutputStream = new FileOutputStream(new File(filePath+fileName));
        } catch (FileNotFoundException e) {
            log.error("cant create filoutputstream", e);
        }
        status = true;
        int id = 1;
        try {
            while(status) {
                byte[] buffer = receiverSlidingWindow.get();
                if (packetSize == 0) {
                    packetSize = buffer.length;
                }
                fileOutputStream.write(buffer);

                if (buffer.length < packetSize) {
                    status = false;
                }
            }
            fileOutputStream.close();
        } catch (IOException e) {
            log.error("problems with filestream in run()", e);
        }
        System.exit(0);
    }
}
