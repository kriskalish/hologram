package net.kalish.hologram.service;

import net.kalish.hologram.service.connector.TcpReceiverConnector;
import net.kalish.hologram.service.connector.TcpSlaveReceiverConnector;
import net.kalish.hologram.service.model.TransactionLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Kris on 12/8/2015.
 */
public class SlaveServiceMain {
    public static void main(String args[]) {
        ExecutorService s = Executors.newFixedThreadPool(5);

        TransactionLog log = new TransactionLog();
        TcpSlaveReceiverConnector clientConnector = new TcpSlaveReceiverConnector(log, LazyConfig.MASTER_TO_SLAVE_PORT);


        s.execute(clientConnector);
    }
}
