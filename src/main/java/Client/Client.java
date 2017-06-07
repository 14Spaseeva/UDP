package Client;

import CommonUtils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by ASPA on 05.05.2017.
 */
//TODO аписать клиента всего и подумать над сервером
public class Client {
    private static Logger log = LoggerFactory.getLogger("client");

    private final ClientSender clientSender;
    private final FileReader reader;
    private final Receiver receiver;

    private final PartOfFileSlidingWidnow slidingWindow;
    private final Channel<byte[]> readingBuffer;
   private DatagramSocket socket;
    private volatile int readingIndex = 1; // 0 - for init package
    private Timer timer = new Timer();

    private static final long TIME_OUT = 1500;


    private Client(String fileName, int slidingWindowSize, int packageSize, InetAddress address, int serverPort) throws IOException {

        MyShutdownHook shutdownHook = new MyShutdownHook();
        Runtime.getRuntime().addShutdownHook(shutdownHook);


        slidingWindow = new PartOfFileSlidingWidnow(slidingWindowSize);
        readingBuffer = new Channel<>(slidingWindowSize);

        for (int i = 0; i < slidingWindowSize; i++)
            readingBuffer.put(new byte[packageSize]);

        File file = new File(fileName);
        socket = new DatagramSocket();


        long packageCount = (long) Math.ceil((double) file.length() / packageSize);

        byte[] initPackageByte = new InitPackage(file.length(), fileName, packageCount + 1).toBytes();
        TimedPartOfFile timedPartOfFile = new TimedPartOfFile(initPackageByte, 0);

        slidingWindow.read(timedPartOfFile);

        clientSender = new ClientSender(
                slidingWindowSize,
                packageSize,
                packageNumber -> slidingWindow.setSendingTime(packageNumber, System.currentTimeMillis()),
                socket,
                address,
                serverPort
        );

        clientSender.sent(timedPartOfFile);

        reader = new FileReader(file, this::getReadingArray, this::onPartRead, this::onEnd);
        receiver = new Receiver(socket, this::onPackageConfirm);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                resend();
            }
        }, TIME_OUT, TIME_OUT);
    }


    private void resend() {
        slidingWindow.getNotConfirmedParts(TIME_OUT).forEach(clientSender::sent);
    }

    private void onEnd() {
        log.info("File has been read");
        reader.stop();
    }

    private void onPartRead(byte[] bytes, int realSize) {
        if (bytes.length != realSize) {
            byte[] newB = new byte[realSize];
            System.arraycopy(bytes, 0, newB, 0, realSize);
            bytes = newB;
        }

        TimedPartOfFile partOfFile = new TimedPartOfFile(bytes, slidingWindow.getCurrentEnd());
        slidingWindow.read(partOfFile);
        clientSender.sent(getDataGram());
    }


    private byte[] getReadingArray() {

        return readingBuffer.get();

    }

    private void onPackageConfirm(int pNubmer) {
        if (slidingWindow.getCurrentStart() > pNubmer)
            return;

        slidingWindow.setConfirm(pNubmer);
        for (TimedPartOfFile p : slidingWindow.moveWindow())
            if (p.number != 0)
                readingBuffer.put(p.data);
    }

    private PartOfFile getDataGram() {
        return slidingWindow.get(readingIndex++);
    }


    private void shutdown() {
        log.info("Shutting down");

        clientSender.stop();
        reader.stop();
        receiver.stop();
        timer.cancel();
        log.info("Good night!");

    }


    public static void main(String[] args) {
        String inputadress = "D:\\UDPSourses\\Concurrency_Lecture_4.pdf";
        try {
            new Client(inputadress, 5, 2000, InetAddress.getLocalHost(), 4444);
        } catch (IOException e) {
            log.error("БУЛЬ БУЛЬ");
        }
    }


    private static class PartOfFileSlidingWidnow extends ConcurrentSlidingWindow<TimedPartOfFile> {

        PartOfFileSlidingWidnow(int size) {
            super(size);
        }

        /**
         * return stream of package without confirmation
         *
         * @param timeOutInMilliseconds timeout after it package should have confirm
         * @return Stream of timeout packages
         */
        Stream<TimedPartOfFile> getNotConfirmedParts(long timeOutInMilliseconds) {
            synchronized (lock) {
                long now = System.currentTimeMillis();
                return IntStream.range(getCurrentStart(), getCurrentEnd())
                        .mapToObj(this::get)
                        .filter(timedPartOfFile -> timedPartOfFile != null
                                && !timedPartOfFile.getConfirm()
                                && timedPartOfFile.gettimeOfSending() != 0
                                && now - timedPartOfFile.gettimeOfSending() > timeOutInMilliseconds);
            }
        }

        /**
         * move window while number of packages in the front of window follow each other
         *
         * @return List of free packages after moving. Reuse data from them
         */
        public List<TimedPartOfFile> moveWindow() {
            synchronized (lock) {
                LinkedList<TimedPartOfFile> result = new LinkedList<>();
                while (get(getCurrentStart()).getConfirm()) {
                    result.add(move());
                }
                return result;
            }
        }

        /**
         * set sending time
         *
         * @param number  package number
         * @param sending time of sending
         */
        public void setSendingTime(int number, long sending) {
            synchronized (lock) {
                if (number >= getCurrentStart()) {
                    try {
                        get(number).settimeOfSending(sending);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        /**
         * Set some package confirm
         *
         * @param number number of package
         */
        public void setConfirm(int number) {
            synchronized (lock) {
                if (number >= getCurrentStart())
                    get(number).setConfirm();
            }
        }

    }


    private class MyShutdownHook extends Thread {
        public void run() {
            shutdown();
        }
    }
}
