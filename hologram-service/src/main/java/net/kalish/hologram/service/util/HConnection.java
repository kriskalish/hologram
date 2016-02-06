package net.kalish.hologram.service.util;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 *
 */
public interface HConnection {

    SelectionKey accept (Selector selector, SocketChannel socketChannel) throws IOException;
    void connect (Selector selector, SocketAddress remoteAddress, int timeout) throws IOException;
    Object readObject() throws IOException;
    void close();
}
