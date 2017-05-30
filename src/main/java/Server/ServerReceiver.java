package Server;

import CommonUtils.Stopable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by ASPA on 05.05.2017.
 */
public class ServerReceiver implements Stopable{
    private static Logger log = LoggerFactory.getLogger("serverReceiver");


    private DatagramSocket datagramSocket;
    private DatagramPacket packet;
    private int bufferSize;
    private boolean isRunning;
    private final ReceiverSlidingWindow receiverSlidingWindow;
    private final ServerSender acknowledgeSender;
    private boolean isAcknInitialized;

    public ServerReceiver(ReceiverSlidingWindow receiverSlidingWindow, ServerSender acknowledgeSender, DatagramSocket datagramSocket) {
        this.receiverSlidingWindow = receiverSlidingWindow;
        this.acknowledgeSender = acknowledgeSender;
        this.datagramSocket = datagramSocket;
        isRunning = false;
        isAcknInitialized = false;
    }


    @Override
    public void stop() {

            isRunning = false;
    }

    @Override
    public void run() {
        try {
            bufferSize = datagramSocket.getReceiveBufferSize();
        } catch (SocketException e) {
            log.error("cant getReceiveBufferSize  ", e);
        }
        isRunning = true;
        byte[] byteIndex = new byte[4];
        byte[] datagramBuffer = new byte[bufferSize];
        packet = new DatagramPacket(datagramBuffer, datagramBuffer.length);
        while(isRunning){
            try {
                datagramSocket.receive(packet);
            } catch (IOException e) {
                log.error("cant recieve datagram  ", e);
            }

            if(!isAcknInitialized){
                acknowledgeSender.init(packet.getAddress());
                isAcknInitialized = true;
            }

            System.arraycopy(datagramBuffer, 0, byteIndex, 0, 4);

            int index = java.nio.ByteBuffer.wrap(byteIndex).getInt();
            acknowledgeSender.push(index);

            if(!receiverSlidingWindow.isPacketReceived(index)){
                int length = packet.getLength();
                byte[] buffer = new byte[length-4];
                System.arraycopy(datagramBuffer, 4, buffer, 0,length-4);
                receiverSlidingWindow.push(buffer, index);
            }
        }
        datagramSocket.close();
    }
}
