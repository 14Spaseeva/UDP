package Server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by ASPA on 05.05.2017.
 */

//TODO +обработка повторных пакетов
public class Server {
    private static Logger log = LoggerFactory.getLogger("server" );

    /*private Sender sender;
    private Receiver receiver;
    */
    private int port;
    String filePath;
    int answerPort; // порт передачи подтверждений

    private final byte packageSize = (byte) 2000;
    private DatagramSocket receiveSocket;
    private DatagramSocket acknowledgeSocket;

    Server(int port, int sendPort, String filePath) {
        this.port = port;
        this.answerPort = sendPort;
        this.filePath=filePath;
    }

    void launch() {


        try {
            receiveSocket = new DatagramSocket(port);
            acknowledgeSocket = new DatagramSocket();


            ReceiverSlidingWindow receiverSlidingWindow = new ReceiverSlidingWindow();

            ServerSender acknowledgeSender = new ServerSender(acknowledgeSocket, answerPort);
            ServerReceiver clientReceiver = new ServerReceiver(receiverSlidingWindow, acknowledgeSender, receiveSocket);
            FileWriter fileWriter = new FileWriter(filePath, receiverSlidingWindow);

            Thread receiverThread = new Thread(clientReceiver);
            Thread fileWriterThread = new Thread(fileWriter);

            receiverThread.start();
            fileWriterThread.start();

            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    acknowledgeSender.stop();
                    clientReceiver.stop();
                }
            });
        } catch (SocketException e) {
            log.error("cant create datagram socket ", e);

        }
    }

    public static void main(String[] args) {
        Server server = new Server(8888, 7777, "D:\\Receiver\\");
        server.launch();

    }

}
