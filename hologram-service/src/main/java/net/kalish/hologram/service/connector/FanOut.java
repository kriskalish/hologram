package net.kalish.hologram.service.connector;

import net.kalish.hologram.service.model.ServiceMessage;
import net.kalish.hologram.service.model.Transaction;
import net.kalish.hologram.service.model.TransactionLog;
import org.msgpack.MessagePack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class FanOut implements Runnable {

    volatile boolean isRunning = true;
    TransactionLog log;
    List<SenderConnector> senders;

    public FanOut(TransactionLog log) {
        this.log = log;
        this.senders = Collections.synchronizedList(new ArrayList<SenderConnector>());
    }

    public void addSender(SenderConnector sc) {
        senders.add(sc);
    }

    public void run() {

        MessagePack mp = new MessagePack();
        //Packer p = mp.createBufferPacker();

        while(isRunning) {
            try {
                Transaction t = log.take();
                byte[] bytes = mp.write(t);

                ServiceMessage csm = new ServiceMessage(t.id, bytes);

                for (SenderConnector sc : senders) {
                    sc.pushToSlave(csm);
                }
            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
}
