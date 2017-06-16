package Client;

import CommonUtils.Cancable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.function.Supplier;

/**
 * Created by ASPA on 05.05.2017.
 */

    public class FileReader implements Cancable {

    private Thread thread;
    private volatile boolean active = true;

    public FileReader(File file, Supplier<byte[]> sourceArray, CallBack confirmation, Runnable onEnd)
            throws FileNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(file);
        thread = new Thread(() -> {
            while (true) {
                if (!active)
                    break;
                try {
                    byte[] bytes = sourceArray.get();
                    int read = fileInputStream.read(bytes);
                    if (read == -1) {
                        onEnd.run();
                        break;
                    } else {
                        confirmation.onRead(bytes, read);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public void cancel() {
        active = false;
        thread.interrupt();
    }

    public interface CallBack {
        void onRead(byte[] data, int realSize);
    }
}



