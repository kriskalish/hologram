package net.kalish.hologram.service;

import net.kalish.hologram.service.connector.TcpReceiverConnector;
import net.kalish.hologram.service.model.TransactionLog2;
import net.kalish.hologram.service.util.NioServer;
import net.kalish.hologram.service.util.ServerToLog;

import java.net.InetSocketAddress;
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
public class MasterServiceMain3 {
    public static void main(String args[]) {
        try {
            ExecutorService s = Executors.newFixedThreadPool(5);

            TransactionLog2 log = new TransactionLog2();
            ServerToLog stl = new ServerToLog(log);

            NioServer server = new NioServer();


            server.addListener(stl);
            server.init(new InetSocketAddress(8989));

            s.execute(server);

        } catch(Exception e) {
            e.printStackTrace();
        }

    }
}
