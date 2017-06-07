package Server;

import CommonUtils.Cancable;
import CommonUtils.Channel;
import CommonUtils.InitPackage;
import CommonUtils.PartOfFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by ASPA on 05.05.2017.
 */
public class FileWriter implements Cancable{
    private static Logger log = LoggerFactory.getLogger("fileWriter");

    private Thread thread;
    private final Channel<PartOfFile> chanel;
    private volatile boolean active = true;

    FileWriter(int maxChannelSize) {
        chanel = new Channel<>(maxChannelSize);
    }

    void init(InitPackage initPackage) {
        thread = new Thread(() -> {
            try {
                File file = new File(initPackage.getFileName());
                OutputStream outputStream = new FileOutputStream("send" + file.getName());
                while (active) {
                    try {
                        PartOfFile partOfFile = chanel.get();
                        outputStream.write(partOfFile.data);
                        if (initPackage.totalPackageCount - 1 == partOfFile.number) {
                            stop();
                        }
                    } catch ( IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        });
        thread.start();
    }

    @Override
    public void stop() {
        active = false;
        thread.interrupt();
        log.info("Writer end work");
    }

    void write(PartOfFile bytes) {
        chanel.put(bytes);
    }
}
