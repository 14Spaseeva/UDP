package Client;

import CommonUtils.ByteUtil;
import CommonUtils.Cancable;
import CommonUtils.Channel;
import CommonUtils.PartOfFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.function.Consumer;

/**
 * Created by ASPA on 05.05.2017.
 */
//оставить работу только отправки файлов

//обьявляется в клиенте
//читает из буфера, преобразовывает (+ номер пакета и тд и превращает в датаграмму), отправляет датаграмму серверу
public class ClientSender implements Cancable {

    private Thread thread;
    private volatile boolean active = true;
    private final Channel<PartOfFile> packetChannel;
    private final byte[] buffer;


    public ClientSender(int chanalSize, int packageSize,
                        Consumer<Integer> sendingConfirmation,
                        DatagramSocket socket,
                        InetAddress address,
                        int port) throws SocketException {
        packetChannel = new Channel<>(chanalSize);

        thread = new Thread(() -> {
            while (true) {
                if (!active || socket.isClosed())
                    break;
                try {
                    DatagramPacket packet;
                    PartOfFile partOfFile = packetChannel.get();
                    packet = wrapPartOfFile(partOfFile);
                    packet.setPort(port);
                    packet.setAddress(address);
                    socket.send(packet);
                    sendingConfirmation.accept(partOfFile.number);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        buffer = new byte[packageSize + 4];
    }

    public void sent(PartOfFile packet) {
        packetChannel.put(packet);
    }

    public DatagramPacket wrapPartOfFile(PartOfFile partOfFile) {

        byte allBytes[] = buffer;
        byte number[] = ByteUtil.intToByteArray(partOfFile.number);

        //copy number to 0 - 4 bytes
        System.arraycopy(number, 0, allBytes, 0, 4);

        //copy data to 4 - last bytes
        System.arraycopy(partOfFile.data, 0, allBytes, 4, partOfFile.data.length);

        return new DatagramPacket(allBytes,  partOfFile.data.length + 4);
    }

    @Override
    public void cancel() {
        active = false;
        thread.interrupt();
        System.out.println("ClientSender end work");
    }
}
