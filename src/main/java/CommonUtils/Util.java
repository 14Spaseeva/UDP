package CommonUtils;

import java.io.*;

/**
 * Created by ASPA on 05.06.2017.
 */
class Util {
    public static byte[] toBytes(Serializable serializable) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = new ObjectOutputStream(bos);
        out.writeObject(serializable);
        return bos.toByteArray();
    }

    public static <T extends Serializable> T convertFromBytes(byte[] bytes) throws IOException {
        try (ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             ObjectInput in = new ObjectInputStream(bis)) {
            return (T) in.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
