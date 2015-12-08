package net.kalish.hologram.service;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 */
public class TcpClientConnector implements Runnable {

    private int mainPort = 8989; // todo need some configuration
    private ServerSocket sock;
    private TransactionLog log;

    private volatile boolean isRunning = true;


    public TcpClientConnector(TransactionLog log) {
        this.log = log;
    }

    public void run() {
        while (isRunning) {
            try {
                if (sock == null || !sock.isBound()) {
                    System.out.println("Creating new server socket...");
                    sock = new ServerSocket(mainPort);
                }

                Socket s = sock.accept();

                InputStream is = s.getInputStream();
                ObjectInputStream ois = new ObjectInputStream(is);

                while(isRunning) {
                    Transaction t = (Transaction) ois.readObject();
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
