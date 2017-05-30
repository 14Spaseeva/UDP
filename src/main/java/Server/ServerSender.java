package Server;

import CommonUtils.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

/**
 * Created by ASPA on 05.05.2017.
 */
public class ServerSender {
    private static Logger log = LoggerFactory.getLogger("serverSender");

    private final int port;
    private Channel channel;
    private DatagramSocket datagramSocket;
    private DatagramPacket packet;
    private boolean isRunning = false;
    private Thread sender = new Thread(new Runnable() {
        @Override
        public void run() {
            while(isRunning){
                Integer index = (Integer)channel.get();
                byte[] buffer = ByteBuffer.allocate(4).putInt(index).array();
                packet.setData(buffer);
                try {
                    datagramSocket.send(packet);
                } catch (IOException e) {
                    log.error("cant send packet  ", e);

                }
            }
            datagramSocket.close();
        }
    });

    public ServerSender(DatagramSocket datagramSocket, int port){
        channel = new Channel(2000000000);
        this.datagramSocket = datagramSocket;
        this.port = port;
    }

    public void push(Integer index){
        channel.put(index);
    }

    public void init(InetAddress inetAddress) // для инициализации адреса для отправки
    {
        packet = new DatagramPacket(new byte[4], 4,inetAddress, port);
        isRunning = true;
        sender.start();
    }

    public void stop(){
        isRunning = false;
    }
}
