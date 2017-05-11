package Server;

/**
 * Created by ASPA on 05.05.2017.
 */

//TODO +обработка повторных пакетов
public class Server {
    /*private Sender sender;
    private Receiver receiver;
    */
    private int port = 6666;
    private final byte packageSize = (byte) 2000;

    Server(int port) {
        this.port = port;
    }

    public static void main(String[] args) {

    }

}
