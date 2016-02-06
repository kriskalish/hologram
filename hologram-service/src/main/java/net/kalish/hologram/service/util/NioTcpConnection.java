package net.kalish.hologram.service.util;

import sun.util.resources.cldr.so.CurrencyNames_so;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 *
 */
public class NioTcpConnection implements HConnection {
    private final boolean DEBUG = true;

    private ByteBuffer readBuffer;
    private ByteBuffer writeBuffer;
    private int curObjectLength;
    private SocketChannel sc;
    private CustomSerializer serializer;
    private SelectionKey selectionKey;


    public NioTcpConnection() {
        writeBuffer = ByteBuffer.allocate(4096);
        readBuffer = ByteBuffer.allocate(4096);
        readBuffer.flip();
    }

    @Override
    public SelectionKey accept (Selector selector, SocketChannel socketChannel) throws IOException {
        writeBuffer.clear();
        readBuffer.clear();
        readBuffer.flip();
        curObjectLength = 0;
        try {
            this.sc = socketChannel;
            socketChannel.configureBlocking(false);
            Socket socket = socketChannel.socket();
            socket.setTcpNoDelay(true);

            selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);

            if (DEBUG) {
                System.out.println("Port " + socketChannel.socket().getLocalPort() + "/TCP connected to: " + socketChannel.socket().getRemoteSocketAddress());
            }

            return selectionKey;
        } catch (IOException ex) {
            close();
            throw ex;
        }
    }

    @Override
    public void connect(Selector selector, SocketAddress remoteAddress, int timeout) throws IOException {
        close();
        writeBuffer.clear();
        readBuffer.clear();
        readBuffer.flip();
        curObjectLength = 0;
        try {
            SocketChannel socketChannel = selector.provider().openSocketChannel();
            Socket socket = socketChannel.socket();
            socket.setTcpNoDelay(true);

            socket.connect(remoteAddress, timeout); // Connect using blocking mode for simplicity.
            socketChannel.configureBlocking(false);
            this.sc = socketChannel;

            selectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
            selectionKey.attach(this);

            if (DEBUG) {
                System.out.println("Port " + socketChannel.socket().getLocalPort() + "/TCP connected to: "
                        + socketChannel.socket().getRemoteSocketAddress());
            }

        } catch (IOException ex) {
            close();
            IOException ioEx = new IOException("Unable to connect to: " + remoteAddress);
            ioEx.initCause(ex);
            throw ioEx;
        }
    }

    @Override
    public Object readObject() throws IOException {

        // by convention we set curObjectLength to 0 after we finish reading an object
        // which means that we need to read the length of the next object
        if(curObjectLength == 0) {

            // If the space left in the buffer is less than the length of an int, compact it and read
            if(readBuffer.remaining() < Integer.BYTES) {
                readBuffer.compact();
                int bytesRead = sc.read(readBuffer);
                readBuffer.flip();

                if(bytesRead == -1) throw new SocketException("Connection closed...");

                if(readBuffer.remaining() < Integer.BYTES) throw new SocketException("Should never happen");
            }

            curObjectLength = readBuffer.getInt();

            if(curObjectLength < 0) throw new SocketException("Invalid object length " + curObjectLength);
            if(curObjectLength > readBuffer.capacity()) throw new SocketException("Unable to read object larger than " + readBuffer.capacity());
        }

        int length = curObjectLength;
        if(readBuffer.remaining() < length) {
            readBuffer.compact();
            int bytesRead = sc.read(readBuffer);
            readBuffer.flip();
            if(bytesRead == -1) throw new SocketException("Conection closed...");
            if(readBuffer.remaining() < length) throw new SocketException("Should never happen");
        }

        curObjectLength = 0;

        int startPosition = readBuffer.position();
        int oldLimit =  readBuffer.limit();
        readBuffer.limit(startPosition + length);

        Object object;
        try {
            object = serializer.deserialize(length, readBuffer);
        } catch (Exception ex) {
            throw new SocketException("Error during deserialization.");
        }

        readBuffer.limit(oldLimit);
        if (readBuffer.position() - startPosition != length)
            throw new SocketException("Incorrect number of bytes (" + (startPosition + length - readBuffer.position()) + " remaining) used to deserialize object: " + object);

        return object;
    }

    @Override
    public void close() {
        System.err.println("close() not implemented");
    }
}
