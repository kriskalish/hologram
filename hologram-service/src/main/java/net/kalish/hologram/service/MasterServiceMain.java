package net.kalish.hologram.service;

import net.kalish.hologram.service.connector.FanOut;
import net.kalish.hologram.service.connector.TcpReceiverConnector;
import net.kalish.hologram.service.connector.TcpSenderConnector;
import net.kalish.hologram.service.model.TransactionLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Here's where the master service starts execution.
 */
public class MasterServiceMain {
    public static void main(String args[]) {
        ExecutorService s = Executors.newFixedThreadPool(5);

        TransactionLog log = new TransactionLog();
        TcpReceiverConnector clientConnector = new TcpReceiverConnector(log, LazyConfig.DEFAULT_PORT);

        FanOut fanOut = new FanOut(log);
        TcpSenderConnector firstSlaveSender = new TcpSenderConnector(LazyConfig.MASTER_TO_SLAVE_PORT);
        fanOut.addSender(firstSlaveSender);


        s.execute(clientConnector);
        s.execute(firstSlaveSender);
        s.execute(fanOut);

    }
}
