package Client;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by ASPA on 05.05.2017.
 */
//TODO аписать клиента всего и подумать над сервером
public class Client {
    private Receiver receiver;
    private int port = 6666;
    private String adress;
    private String fileName;
    private final int packageSize = 2000; //размер блока


//определеять метод интрефейса callback

    private ObjectInputStream objIn;
    private ObjectOutputStream objOut;

    InetAddress IPAddress;

    long packageCount;
    File fileToTransfer;

    Client(String address, int port, String fileName) {
        this.adress = address;
        this.port = port;
        this.fileName = fileName;
        try {
            IPAddress = InetAddress.getByName("localhost");
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        fileToTransfer = new File(fileName); //для передачи в самой первой датаграмме
        packageCount = (long) Math.ceil((double) fileToTransfer.length() / packageSize);//кол-во пакетов

        InitPackage initPackage = new InitPackage(fileToTransfer.length(), fileName, packageCount ); //что за filesize?


    }

    FileReader(this::onRead, )
        void onRead(byte[] ..)
//со стороны клиента определяем метод callback-а
    public static void main(String[] args) {

    }
}
