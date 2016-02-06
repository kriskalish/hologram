package net.kalish.hologram.service.util;

import java.nio.ByteBuffer;

/**
 * Created by kris on 2/5/16.
 */
public interface Serializer {
    Object deserialize(int length, ByteBuffer buffer);
}
