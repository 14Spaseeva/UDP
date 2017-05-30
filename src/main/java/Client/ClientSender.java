package Client;

import CommonUtils.Channel;
import CommonUtils.Stopable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * Created by ASPA on 05.05.2017.
 */
//оставить работу только отправки файлов

    //обьявляется в клиенте
//читает из буфера, преобразовывает (+ номер пакета и тд и превращает в датаграмму), отправляет датаграмму серверу
public class ClientSender implements Stopable {
    private static Logger log = LoggerFactory.getLogger("clientSender");


    private DatagramSocket datagramSocket;
    private SlidingWindowController slidingWindowController;
    private final InetAddress inetAddress;
    private final int port;
    private boolean status;

    private Channel<Object> channel;

    ClientSender(SlidingWindowController slidingWindowController, DatagramSocket datagramSocket,
           InetAddress inetAddress, int port){

        // buffer(byte[]) - из FileReader

        //разбиваем по пакетам
        //+  берем из Channel<T> из ЛР1 PartofFile, преобразуем в одну последовательность байт и отправляем

        status=true;
        this.slidingWindowController = slidingWindowController;
        this.datagramSocket = datagramSocket;
        this.inetAddress = inetAddress;
        this.port = port;

    }

    @Override
    public void stop() {
        status=false;
        datagramSocket.close();

    }

    @Override
    public void run() {
        DatagramPacket packet = new DatagramPacket(new byte[1], 1, inetAddress, port);
        while(status) {
            byte[] buffer = slidingWindowController.getBytes();
            packet.setData(buffer);
            packet.setLength(buffer.length);
            try {
                datagramSocket.send(packet);
            } catch (IOException e) {
                  log.error("cant send packet", e);
            }
        }
    }
}
