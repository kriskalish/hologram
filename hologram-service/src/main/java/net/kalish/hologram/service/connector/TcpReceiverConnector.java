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
public class TcpReceiverConnector implements Runnable {

    private int mainPort = 8989; // todo need some configuration
    private ServerSocket sock;
    private TransactionLog log;

    private volatile boolean isRunning = true;


    public TcpReceiverConnector(TransactionLog log, int port) {
        this.log = log;
        this.mainPort = port;
    }

    public void run() {
        while (isRunning) {
            try {
                if (sock == null || !sock.isBound()) {
                    System.out.println("Creating new server socket...");
                    sock = new ServerSocket(mainPort);
                }

                Socket s = sock.accept();

                InputStream is = new FastBufferedInputStream(s.getInputStream());
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
