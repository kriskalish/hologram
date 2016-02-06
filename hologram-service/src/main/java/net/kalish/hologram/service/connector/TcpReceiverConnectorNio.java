package net.kalish.hologram.service.connector;

import net.kalish.hologram.service.model.TransactionLog;
import org.msgpack.MessagePack;
import org.msgpack.unpacker.Unpacker;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.Set;

/**
 *
 */
public class TcpReceiverConnectorNio implements Runnable {

    private int mainPort = 8989; // todo need some configuration
    private TransactionLog log;

    private ByteBuffer echoBuffer = ByteBuffer.allocate(1024);

    private volatile boolean isRunning = true;


    public TcpReceiverConnectorNio(TransactionLog log, int port) {
        this.log = log;
        this.mainPort = port;
    }



    public void run() {
        try {

            // Create a new selector
            Selector selector = Selector.open();

            // Open a listener on each port, and register each one
            // with the selector

            {
                ServerSocketChannel ssc = ServerSocketChannel.open();
                ssc.configureBlocking(false);
                ServerSocket ss = ssc.socket();
                InetSocketAddress address = new InetSocketAddress(mainPort);
                ss.bind(address);

                SelectionKey key = ssc.register(selector, SelectionKey.OP_ACCEPT);
            }

            System.out.println("Going to listen on " + mainPort);

            MessagePack mp = new MessagePack(); Unpacker up = mp.createUnpacker(null);

            while (isRunning) {

                int num = selector.select();

                Set selectedKeys = selector.selectedKeys();
                Iterator it = selectedKeys.iterator();

                while (it.hasNext()) {
                    SelectionKey key = (SelectionKey) it.next();

                    if ((key.readyOps() & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                        // Accept the new connection
                        ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                        SocketChannel sc = ssc.accept();
                        sc.configureBlocking(false);

                        // Add the new connection to the selector
                        SelectionKey newKey = sc.register(selector, SelectionKey.OP_READ);
                        it.remove();

                        System.out.println("Got connection from " + sc);
                    } else if ((key.readyOps() & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                        // Read the data
                        SocketChannel sc = (SocketChannel) key.channel();

                        // Echo data
                        int bytesEchoed = 0;
                        while (true) {
                            echoBuffer.clear();

                            int number_of_bytes = sc.read(echoBuffer);

                            if (number_of_bytes <= 0) {
                                break;
                            }

                            echoBuffer.flip();

                            sc.write(echoBuffer);
                            bytesEchoed += number_of_bytes;
                        }

                        System.out.println("Echoed " + bytesEchoed + " from " + sc);

                        it.remove();
                    }
                }
            }

        } catch(Exception e) {
            System.err.println(e);
        }
    }

    public Object readObject(SocketChannel sc) {
        return null;
    }
}
