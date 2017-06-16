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
class Writer implements Cancable {
    private Thread thread;
    private final Channel<PartOfFile> chanel;
    private volatile boolean active = true;

    public Writer(int maxChannelSize) {
        chanel = new Channel<>(maxChannelSize);
    }
    public void init(InitPackage initPackage) {
        thread = new Thread(() -> {
            try {
                File file = new File(initPackage.fileName);
                OutputStream outputStream = new FileOutputStream("send" + file.getName());
                while (active) {
                    try {
                        PartOfFile partOfFile = chanel.get();
                        outputStream.write(partOfFile.data);
                        if (initPackage.totalPackageCount - 1 == partOfFile.number) {
                            cancel();
                        }
                    } catch (IOException e) {
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
    public void cancel() {
        active = false;
        thread.interrupt();
        System.out.println("Writer is stopped");
    }

    public void write(PartOfFile bytes) {
        chanel.put(bytes);
    }
}