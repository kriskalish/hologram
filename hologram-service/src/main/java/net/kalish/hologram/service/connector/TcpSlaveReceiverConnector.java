package net.kalish.hologram.service.connector;

import it.unimi.dsi.fastutil.io.FastBufferedInputStream;
import net.kalish.hologram.service.model.Transaction;
import net.kalish.hologram.service.model.TransactionLog;
import org.msgpack.MessagePack;
import org.msgpack.unpacker.Unpacker;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
public class TcpSlaveReceiverConnector implements Runnable {

    private int mainPort;
    private Socket sock;
    private TransactionLog log;

    private volatile boolean isRunning = true;


    public TcpSlaveReceiverConnector(TransactionLog log, int port) {
        this.log = log;
        this.mainPort = port;
    }

    public void run() {
        while (isRunning) {
            try {
                if (sock == null || !sock.isBound()) {
                    System.out.println("Creating new server socket...");
                    sock = new Socket("127.0.0.1", mainPort);
                }


                InputStream is = new FastBufferedInputStream(sock.getInputStream());
                //ObjectInputStream ois = new ObjectInputStream(is);
                //Kryo k = new Kryo(); Input i = new Input(is);

                MessagePack mp = new MessagePack(); Unpacker up = mp.createUnpacker(is);


                while(isRunning) {

                    Transaction t = up.read(Transaction.class);
                    //Transaction t = k.readObject(i, Transaction.class);
                    //Transaction t = (Transaction) ois.readObject();
                    t.id = log.getNextId();
                    //System.out.println("Received " + t);
                    log.append(t);


                }


            } catch(Exception e) {
                System.err.println(e);
            }
        }
    }
}
