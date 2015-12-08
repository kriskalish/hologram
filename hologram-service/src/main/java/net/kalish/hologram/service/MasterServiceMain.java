package net.kalish.hologram.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Here's where the master service starts execution.
 */
public class MasterServiceMain {
    public static void main(String args[]) {
        ExecutorService s = Executors.newFixedThreadPool(5);

        TransactionLog log = new TransactionLog();
        TcpClientConnector clientConnector = new TcpClientConnector(log);

        s.execute(clientConnector);
    }
}
