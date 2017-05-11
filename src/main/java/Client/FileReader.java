package Client;

/**
 * Created by ASPA on 05.05.2017.
 */
public class FileReader {
    Thread thread;

    FileReader(){
        thread = new Thread(()-> {
            //TODO  в Receiver , Sender тоже
            //читает данные из фпайлв и склыдывает их в буфер

        });
    }
}
