package Client;

import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by ASPA on 05.05.2017.
 */
//TODO аписать клиента всего и подумать над сервером
public class Client {
    private Receiver receiver;
    private int port = 6666;
    private String adress;
    private String fileName;
    private  final int  packageSize =  2000;


    private ObjectInputStream objIn;
    private ObjectOutputStream objOut;


    Client(String address, int port, String fileName){
        this.adress=address;
        this.port=port;
        this.fileName=fileName;

        File fileToTransfer = new File(fileName); //для передачи в самой первой датаграмме
        // fileToTransfer.length() -int
        long packageCount; //кол-во пакетов
        packageCount = (long) Math.ceil((double) fileToTransfer.length()/packageSize);

       //TODO InitPackage initPackage = new InitPackage();



    }

    public static void main(String[] args) {

    }
}
