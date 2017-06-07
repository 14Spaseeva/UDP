package Server;

import CommonUtils.Cancable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Created by ASPA on 05.05.2017.
 */
public class ServerReceiver implements Cancable{
    private static Logger log = LoggerFactory.getLogger("serverReceiver");

    private final Thread thread;
    private volatile boolean active = true;

    ServerReceiver(DatagramSocket socket, Consumer<DatagramPacket> datagramPacketConsumer, Supplier<byte[]> supplier) {
        thread = new Thread(() -> {
            while (active) {
                try {
                    byte[] bytes = supplier.get();
                    DatagramPacket datagramPacket = new DatagramPacket(bytes, bytes.length);
                    socket.receive(datagramPacket);
                    datagramPacketConsumer.accept(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public void stop() {
        active = false;
        thread.interrupt();
    }
}
