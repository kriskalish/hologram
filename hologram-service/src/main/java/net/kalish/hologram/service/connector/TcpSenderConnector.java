package net.kalish.hologram.service.connector;

import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import net.kalish.hologram.service.model.CrossServiceMessage;
import net.kalish.hologram.service.model.Transaction;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * A runnable that listens for incoming connections from slaves.
 */
public class TcpSenderConnector implements Runnable, SenderConnector {
    private int masterToSlavePort;
    private ServerSocket sock;

    private BlockingQueue<CrossServiceMessage> queue;

    private volatile boolean isRunning = true;


    public TcpSenderConnector(int port) {
        queue = new ArrayBlockingQueue<CrossServiceMessage>(1024*64);
        this.masterToSlavePort = port;
    }

    public void pushToSlave(CrossServiceMessage m) throws InterruptedException {
        queue.put(m);
    }


    public void run() {
        while (isRunning) {
            try {
                if (sock == null || !sock.isBound()) {
                    System.out.println("Creating new server socket...");
                    sock = new ServerSocket(masterToSlavePort);
                }

                Socket s = sock.accept();
                OutputStream os = new FastBufferedOutputStream(s.getOutputStream());

                while(isRunning) {
                    CrossServiceMessage m = queue.take();

                    os.write(m.serializedTransaction);
                }
            } catch(Exception e) {
                System.err.println(e);
            }
        }
    }
}
