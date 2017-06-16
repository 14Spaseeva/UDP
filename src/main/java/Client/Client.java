package Client;

import CommonUtils.Channel;
import CommonUtils.ConcurrentSlidingWindow;
import CommonUtils.InitPackage;
import CommonUtils.PartOfFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by ASPA on 05.05.2017.
 */
public class Client {
    private static Logger log = LoggerFactory.getLogger("client");

    private final ClientSender clientSender;
    private final FileReader reader;
    private final Receiver receiver;

    private final ConcurrentSlidingWindow slidingWindow;
    private final Channel<byte[]> readingBuffer;

    private volatile int readingIndex = 1; // 0 - for init package
    private Timer timer = new Timer();

    private static final long TIME_OUT = 1500;


    public Client(String fileName, int slidingWindowSize, int packageSize, InetAddress address, int serverPort) throws IOException {
        MyShutdownHook shutdownHook = new MyShutdownHook();
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        slidingWindow = new ConcurrentSlidingWindow(slidingWindowSize);
        readingBuffer = new Channel<>(slidingWindowSize);

        for (int i = 0; i < slidingWindowSize; i++)
            readingBuffer.put(new byte[packageSize]);

        File file = new File(fileName);
        DatagramSocket socket = new DatagramSocket();


        long numberOfPackages = roundedNatural(file.length(), packageSize);
        long numberOfPackagesPlusInit = numberOfPackages + 1;

        byte[] initPackageByte = new InitPackage(file.length(), fileName, numberOfPackagesPlusInit).toBytes();
        PartOfFile init = new PartOfFile(initPackageByte, 0);

        slidingWindow.read(init);

        clientSender = new ClientSender(
                slidingWindowSize,
                packageSize,
                packageNumber -> slidingWindow.setSendingTime(packageNumber, System.currentTimeMillis()),
                socket,
                address,
                serverPort
        );

        clientSender.sent(init);

        reader = new FileReader(file, this::getReadingArray, this::onPartRead, this::onEnd);
        receiver = new Receiver(socket, this::onPackageConfirm);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                resend();
            }
        }, TIME_OUT, TIME_OUT);
    }

    private long roundedNatural(long a, long b) {
        return (a + (b - 1)) / b;
    }

    private byte[] getReadingArray() {
        return readingBuffer.get();
    }

    private void onPartRead(byte[] bytes, int realSize) {
        if (bytes.length != realSize) {
            byte[] newB = new byte[realSize];
            System.arraycopy(bytes, 0, newB, 0, realSize);
            bytes = newB;
        }

        PartOfFile partOfFile = new PartOfFile(bytes, slidingWindow.getCurrentEnd());
        slidingWindow.read(partOfFile);
        clientSender.sent(getDataGram());
    }

    private PartOfFile getDataGram() {
        return slidingWindow.get(readingIndex++);
    }

    private void onEnd() {
        log.info("file is read");
        reader.cancel();
    }

    private void onPackageConfirm(int pNubmer) {
        if (slidingWindow.getCurrentStart() > pNubmer)
            return;

        slidingWindow.setConfirm(pNubmer);
        for (PartOfFile p : slidingWindow.moveWindow())
            if (p.number != 0)
                readingBuffer.put(p.data);
    }

    private void resend() {
        slidingWindow.getNotConfirmedParts(TIME_OUT).forEach(clientSender::sent);
    }

    public static void main(String[] args) throws IOException {
        String inputFile = "D:\\UDPSourses\\Concurrency_Lecture_4.pdf";
        int serverPort = 6666;
        int packageSize = 2000;
        int slidingWindowSize = 5;

        new Client(inputFile, slidingWindowSize, packageSize, InetAddress.getLocalHost(), serverPort);

    }

    private class MyShutdownHook extends Thread {
        public void run() {
            shutdown();
        }

    }

    private void shutdown() {

        clientSender.cancel();
        reader.cancel();
        receiver.cancel();
        timer.cancel();
    }
}
