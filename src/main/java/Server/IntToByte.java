package Server;

import java.nio.ByteBuffer;

/**
 * Created by ASPA on 05.05.2017.
 */
public class IntToByte {
    private byte[] byteVal;
    private int intVal;

    //TODO byteval ->byte[] , intVal-> byte[], byteval[]+intval[]
    IntToByte(int val) {

        byte[] bytes = ByteBuffer.allocate(4).putInt(val).array();
    }

    IntToByte(byte[] bytes)
    {
        //byteVal = ByteBuffer.wrap(bytes);
    }
}
