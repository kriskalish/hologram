package net.kalish.hologram.service.connector;

import net.kalish.hologram.service.Transaction;
import net.kalish.hologram.service.TransactionLog;
import org.msgpack.MessagePack;
import org.msgpack.unpacker.Unpacker;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 *
 */
public class TcpMasterToSlaveConnector {
    private int masterToSlavePort = 8990; // todo need some configuration
    private ServerSocket sock;

    private BlockingQueue<Transaction> queue;

    private volatile boolean isRunning = true;


    public TcpMasterToSlaveConnector() {
        queue = new ArrayBlockingQueue<Transaction>(1024*64);
    }

    public void run() {
        while (isRunning) {
            try {
                if (sock == null || !sock.isBound()) {
                    System.out.println("Creating new server socket...");
                    sock = new ServerSocket(masterToSlavePort);
                }

                Socket s = sock.accept();

                InputStream is = new BufferedInputStream(s.getInputStream());
                MessagePack mp = new MessagePack();
                Unpacker up = mp.createUnpacker(is);


                while(isRunning) {

                    Transaction t = up.read(Transaction.class);
                    //Transaction t = k.readObject(i, Transaction.class);
                    //Transaction t = (Transaction) ois.readObject();
                    //t.id = log.getNextId();
                    //System.out.println("Received " + t);
                    //log.append(t);


                }


            } catch(Exception e) {
                System.err.println(e);
            }
        }
    }
}
