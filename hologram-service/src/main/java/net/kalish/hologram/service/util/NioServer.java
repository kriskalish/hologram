package net.kalish.hologram.service.util;

import net.kalish.hologram.service.model.ServiceMessage;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 *
 */
public class NioServer implements Runnable {
    private final boolean DEBUG = true;

    private Selector selector;
    private ServerSocketChannel ssc;





    public void init(InetSocketAddress address) throws IOException {
        selector = Selector.open();
        ssc = selector.provider().openServerSocketChannel();
        ssc.socket().bind(address);
        ssc.configureBlocking(false);
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        if(DEBUG) System.out.println("NioServer started on address " + address);

    }

    public void poll(int timeout) throws Exception {
        int numSelected = 0;

        numSelected = selector.select(timeout);

        if(numSelected == 0)
            return;

        Set<SelectionKey> selectedKeys = selector.selectedKeys();

        for(Iterator<SelectionKey> iter = selectedKeys.iterator(); iter.hasNext();) {
            SelectionKey curKey = iter.next();
            iter.remove();

            HConnection conn = (HConnection) curKey.attachment();
            int ops = curKey.readyOps();

            if ((ops & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT) {
                SocketChannel socketChannel = ssc.accept();
                if (socketChannel != null) handleAccept(socketChannel);
                continue;
            }

            // if there's no associated connection, just skip it
            if(conn == null) {
                if(DEBUG) System.out.println("Skipping selection key...");
                continue;
            }


            if ((ops & SelectionKey.OP_READ) == SelectionKey.OP_READ) {
                ServiceMessage msg = (ServiceMessage) conn.readObject();
            }
            /*
            if ((ops & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE) {

            }
            */
        }
    }

    @Override
    public void run() {
        try {
            while(true) {
                poll(200);
            }

        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void handleAccept(SocketChannel sc) {
        HConnection connection = new NioTcpConnection();

        try {
            SelectionKey selectionKey = connection.accept(selector, sc);
            selectionKey.attach(connection);

            // todo: do some accounting here
        } catch (IOException ex) {
            connection.close();
            if (DEBUG) System.out.println("Unable to accept TCP connection.");
        }
    }
}
