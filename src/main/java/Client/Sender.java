package Client;

import CommonUtils.Channel;

/**
 * Created by ASPA on 05.05.2017.
 */
//оставить работу только отправки файлов

    //обьявляется в клиенте
//читает из буфера, преобразовывает (+ номер пакета и тд и превращает в датаграмму), отправляет датаграмму серверу
public class Sender {
    private Channel<InitPackage> channel;

    Sender(){
        // buffer(byte[]) - из FileReader

        //разбиваем по пакетам
        //+  берем из Channel<T> из ЛР1 PartofFile, преобразуем в одну последовательность байт и отправляем

    }
}
