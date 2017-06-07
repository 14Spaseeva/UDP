package Server;

import CommonUtils.ByteUtil;
import CommonUtils.Cancable;
import CommonUtils.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

/**
 * Created by ASPA on 05.05.2017.
 */
public class Sender implements Cancable{
    private static Logger log = LoggerFactory.getLogger("serverSender");

    private Thread thread;
    private volatile boolean active = true;
    private final Channel<Integer> packetChannel;
    private final DatagramSocket socket;


    Sender(int chanalSize,
           DatagramSocket socket
    ) throws SocketException {
        packetChannel = new Channel<>(chanalSize);
        this.socket = socket;
    }

    void init( InetAddress address,
               int port) {
        if (thread == null) {
            thread = new Thread(() -> {
                while (true) {
                    if (!active || socket.isClosed())
                        break;
                    try {
                        DatagramPacket packet;

                            Integer partOfFile = packetChannel.get();
                            packet = new DatagramPacket(ByteUtil.intToByteArray(partOfFile), 4, address, port);
                            packet.setPort(port);
                            packet.setAddress(address);
                            socket.send(packet);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }

    void sent(Integer packet) {
        packetChannel.put(packet);
    }


    @Override
    public void stop() {
        active = false;
        if (thread != null)
            thread.interrupt();
        System.out.println("Sender end work");
    }
}
