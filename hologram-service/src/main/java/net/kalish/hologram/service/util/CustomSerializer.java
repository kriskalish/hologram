package net.kalish.hologram.service.util;

import net.kalish.hologram.service.model.ServiceMessage;

import java.nio.ByteBuffer;

/**
 * Created by kris on 2/5/16.
 */
public class CustomSerializer implements Serializer {
    @Override
    public Object deserialize(int length, ByteBuffer buffer) {
        int byteArrayLength = length - Long.BYTES;
        byte[] bytes = new byte[byteArrayLength];

        long transactionId = buffer.getLong();
        buffer.get(bytes);
        //buffer.get(bytes, 0, byteArrayLength);

        ServiceMessage m = new ServiceMessage(transactionId, bytes);

        return m;
    }
}
