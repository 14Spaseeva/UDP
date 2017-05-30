package Client;

import CommonUtils.Stopable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by ASPA on 05.05.2017.
 */
public class ClientReceiver implements Stopable {
    //принимает подтверждения о передаче частей файла.
    private boolean status;
    DatagramSocket datagramSocket;
    SlidingWindowController slidingWindowController;

    public ClientReceiver(SlidingWindowController slidingWindowController, DatagramSocket datagramSocket) {
        this.slidingWindowController = slidingWindowController;
        this.datagramSocket = datagramSocket;
        status = true;
    }

    @Override
    public void run() {
        byte[] buffer = new byte[4];
        DatagramPacket packet;
        while (status) {
            packet = new DatagramPacket(buffer, 4);
            try {
                datagramSocket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int index = java.nio.ByteBuffer.wrap(packet.getData()).getInt();
            slidingWindowController.setReceived(index);
        }
    }

    @Override
    public void stop() {
        status = false;
        datagramSocket.close();
    }


}

