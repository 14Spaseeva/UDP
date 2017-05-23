package Client;

import java.io.FileInputStream;
import java.util.ArrayList;

/**
 * Created by ASPA on 05.05.2017.
 */
public class Receiver {
    // принимаем ответ от сервера в виде датаграммы (номер пакета)
    Thread thread;

    ArrayList<byte[]> packages; //части файла

    Receiver() {
        thread = new Thread(() -> {
            //TODO  в Receiver , Sender тоже

            //читает данные из фпайлв и склыдывает их в буфер

        });
    }
}
