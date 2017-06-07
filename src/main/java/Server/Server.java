package Server;


import Client.FileReader;
import CommonUtils.DatagramTranslator;
import CommonUtils.InitPackage;
import CommonUtils.PartOfFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;

/**
 * Created by ASPA on 05.05.2017.
 */

//TODO +обработка повторных пакетов
public class Server {
    private static Logger log = LoggerFactory.getLogger("server" );

    private volatile SortedSet<PartOfFile> partOfFileMap = new TreeSet<>((o1, o2) -> o1.number - o2.number);
    private volatile int writing;

    private FileWriter writer;
    private Sender sender;
    private ServerReceiver reciever;

    private volatile int totalPackages;

    private static final boolean debug = true;
    private volatile boolean init = false;

    private volatile long timeOfStart;

    {
        partOfFileMap.add(new PartOfFile(new byte[0], 0));
    }

    private Server(int packageSize, int port, int slidingWindowSize) throws SocketException {

        MyShutdownHook shutdownHook = new MyShutdownHook();
        Runtime.getRuntime().addShutdownHook(shutdownHook);

        DatagramSocket socket = new DatagramSocket(port);

        reciever = new ServerReceiver(socket, this::onReceive, () -> new byte[packageSize + 4]);
        writer = new FileWriter(slidingWindowSize);
        sender = new Sender(slidingWindowSize, socket);

    }

    private void onReceive(DatagramPacket packet) {
        if (timeOfStart == 0)
            timeOfStart = System.currentTimeMillis();

        try {
            PartOfFile partOfFile = DatagramTranslator.unWrap(packet);

            if (partOfFile.number == 0 && !init) {
                init = true;
                InitPackage initPackage = InitPackage.fromBytes(partOfFile.data);
                totalPackages = (int) (initPackage.totalPackageCount);
                writer.init(initPackage);
                System.out.printf("Start receiving %s size: %d number of packages: %d%n", initPackage.getFileName(), initPackage.getFileSize(), initPackage.totalPackageCount);
            } else
                processPartOfFile(partOfFile);
            sendConfirm(partOfFile.number, packet.getPort(), packet.getAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendConfirm(int number, int port, InetAddress address) throws IOException {
//        log("send confirm" + number);
        sender.init(address, port);
        sender.sent(number);
    }

    private void log(String s) {
        if (debug)
            System.out.println(s);
    }

    private void processPartOfFile(PartOfFile partOfFile) throws IOException {
//        log("processPartOfFile " + partOfFile.number);

        if (writing <= partOfFile.number) {
            partOfFileMap.add(partOfFile);

            Iterator<PartOfFile> iterator = partOfFileMap.iterator();
            if (iterator.hasNext()) {
                PartOfFile current = iterator.next();
                List<PartOfFile> result = getWritePackages(iterator, current);
                partOfFileMap.removeAll(result);
                for (PartOfFile p : result) {
                    write(p);
                    if (partOfFileMap.first().number == totalPackages - 1) {
                        write(partOfFileMap.first());
                        System.out.printf("Success sending of %d packages time:%f %n", totalPackages, (System.currentTimeMillis() - timeOfStart) / 1000f);
                        shutdown();
                    }
                }

            }
        }
    }

    private void write(PartOfFile p) {
        log("write " + p.number);
        writer.write(p);
        writing = p.number;
    }

    private static List<PartOfFile> getWritePackages(Iterator<PartOfFile> iterator, PartOfFile current) {
        if (iterator.hasNext()) {
            PartOfFile next = iterator.next();
            if (current.number + 1 == next.number) {
                List<PartOfFile> result = new LinkedList<>();
                result.add(current);
                result.addAll(getWritePackages(iterator, next));
                return result;
            }
        }
        return Collections.emptyList();
    }


    private void shutdown() {
        log.info("Shutting down");
        reciever.stop();
        log.info("Good night!");

    }


    public static void main(String[] args) {
        try {
            new Server(2000, 4444, 5);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }



    private class MyShutdownHook extends Thread {
        public void run() {
            shutdown();
        }
    }
}
