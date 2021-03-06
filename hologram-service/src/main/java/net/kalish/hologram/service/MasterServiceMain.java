package net.kalish.hologram.service;

import net.kalish.hologram.service.connector.FanOut;
import net.kalish.hologram.service.connector.TcpReceiverConnector;
import net.kalish.hologram.service.connector.TcpSenderConnector;
import net.kalish.hologram.service.model.TransactionLog;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Here's where the master service starts execution.
 *
 * todo:
 * -replace the fan out class with disruptor
 *    - requires dynamically adding/removing handlers see: https://github.com/LMAX-Exchange/disruptor/issues/30
 * -make master store a materialized table (key, byte[])
 * -make slaves store a materialized table as (key, byte[]) or (key, object)
 * -make master actually handle multiple clients and multiple slaves
 * -make master sync table to disk periodically
 * -make slave ask for table on connection
 * -consider using nio sockets
 */
public class MasterServiceMain {
    public static void main(String args[]) {
        ExecutorService s = Executors.newFixedThreadPool(5);

        TransactionLog log = new TransactionLog();
        TcpReceiverConnector clientConnector = new TcpReceiverConnector(log, LazyConfig.DEFAULT_PORT);

        FanOut fanOut = new FanOut(log);
        TcpSenderConnector firstSlaveSender = new TcpSenderConnector(LazyConfig.MASTER_TO_SLAVE_PORT);
        fanOut.addSender(firstSlaveSender);

        Drain d = new Drain(log);
        s.execute(d);


        s.execute(clientConnector);
        s.execute(firstSlaveSender);
        s.execute(fanOut);

    }
}
