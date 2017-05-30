package Client;

import CommonUtils.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by ASPA on 05.05.2017.
 */
//TODO аписать клиента всего и подумать над сервером
public class Client {
    private static Logger log = LoggerFactory.getLogger("client");

    private int port; //порт передачи данных
    private String adress;
    private final int packageSize = 2000; //размер блока
    private int capacity; //ширина окна
    private int timeout;
    private String sendingFilePath; //путь к передаваемому файлу
    private int receiverPort;//порт для приема подтверждений
    private String fileName;
    private FileInputStream fileInputStream;
    private DatagramSocket datagramSocket;
    private DatagramSocket receiverSocket;

    private ObjectInputStream objIn;
    private ObjectOutputStream objOut;

    InetAddress IPAddress;

    long packageCount;
    File fileToTransfer;

    Client(int port, int capacity, String filePAth, int receiverPort) {
        this.port = port;
        this.adress = "localhost";
        this.capacity = capacity;
        this.sendingFilePath = filePAth;
        this.receiverPort = receiverPort;
        timeout = 2;
        try {
            IPAddress = InetAddress.getByName(adress);
        } catch (UnknownHostException e) {
            log.error("", e);
        }


        try {
            fileToTransfer = new File(sendingFilePath); //для передачи в самой первой датаграмме
            fileName = fileToTransfer.getName();
            fileInputStream = new FileInputStream(new File(sendingFilePath));
        } catch (FileNotFoundException e) {
            log.error("", e);
        }

        packageCount = (long) Math.ceil((double) fileToTransfer.length() / packageSize);//кол-во пакетов
    }

    SlidingWindowController slidingWindowController;
    FileReader fileReader;
    ClientSender sender;
    Timer timer;
    ClientReceiver clientReceiver;

    Thread fileReaderThread;
    Thread receiverThread;
    Thread senderThread;
    Thread timerThread;

    Channel channel;

    void launch() {
        channel = new Channel(2000000000);
        try {
            datagramSocket = new DatagramSocket();
            receiverSocket = new DatagramSocket(receiverPort);

            slidingWindowController = new SlidingWindowController(capacity, channel, timeout);
            fileReader = new FileReader(fileInputStream, fileName, packageSize, slidingWindowController);
            sender = new ClientSender(slidingWindowController, datagramSocket, IPAddress, port);
            timer = new Timer(slidingWindowController);
            clientReceiver = new ClientReceiver(slidingWindowController, receiverSocket);

            fileReaderThread = new Thread(fileReader);
            senderThread = new Thread(sender);
            timerThread = new Thread(timer);
            receiverThread = new Thread(clientReceiver);

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    senderThread.interrupt();
                    sender.stop();

                    clientReceiver.stop();
                    receiverThread.interrupt();

                    timer.stop();
                    timerThread.interrupt();
                }
            });

            fileReaderThread.start();
            senderThread.start();
            timerThread.start();
            receiverThread.start();

        } catch (SocketException e) {
            log.error("Cant be connected to port", e);
        }

    }


    public static void main(String[] args) {
        Client client = new Client(8888, 5, "D:\\hospital_lab1.sql", 7777);
        client.launch();

    }
}
