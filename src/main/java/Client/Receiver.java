package Client;

import CommonUtils.Cancable;
import CommonUtils.DatagramTranslator;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * Created by ASPA on 05.05.2017.
 */
public class Receiver implements Cancable {


    private final Thread thread;
    private volatile boolean active = true;
//закидывает куски файла в сервер
    Receiver(DatagramSocket socket, Consumer<Integer> confirm) {
        thread = new Thread(() -> {
            while (active) {
                try {
                    DatagramPacket packet = new DatagramPacket(new byte[4], 4);
                    socket.receive(packet);
                    Integer integer = DatagramTranslator.packageToConfirmation.apply(packet);

                    confirm.accept(integer);

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

