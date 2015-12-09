package net.kalish.hologram.service;

import net.kalish.hologram.service.connector.TcpClientConnector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Kris on 12/8/2015.
 */
public class SlaveServiceMain {
    public static void main(String args[]) {
        ExecutorService s = Executors.newFixedThreadPool(5);

        TransactionLog log = new TransactionLog();
        TcpClientConnector clientConnector = new TcpClientConnector(log);

        s.execute(clientConnector);
    }
}
