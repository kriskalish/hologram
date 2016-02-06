package net.kalish.hologram.service.connector;

import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import net.kalish.hologram.service.model.Transaction;
import net.kalish.hologram.service.model.TransactionLog;
import org.msgpack.MessagePack;
import org.msgpack.unpacker.Unpacker;

import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 *
 */
public class TcpReceiverConnectorNio2 implements Runnable, CompletionHandler<AsynchronousSocketChannel, Object> {

    private int mainPort = 8989; // todo need some configuration
    private TransactionLog log;

    private volatile boolean isRunning = true;


    public TcpReceiverConnectorNio2(TransactionLog log, int port) {
        this.log = log;
        this.mainPort = port;
    }

    public void run() {
        while (isRunning) {
            try {
                SocketAddress address = new InetSocketAddress("localhost", mainPort);
                AsynchronousServerSocketChannel channel = AsynchronousServerSocketChannel.open().bind(address);

                channel.accept(null, null);
            } catch(Exception e) {
                System.err.println(e);
            }
        }
    }

    @Override
    public void failed(Throwable throwable, Object o) {

    }

    @Override
    public void completed(AsynchronousSocketChannel ch, Object o) {
        //ch.read();
    }
}
