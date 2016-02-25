package net.kalish.hologram.service.util;

import com.sun.corba.se.spi.activation.Server;
import net.kalish.hologram.service.model.ServiceMessage;
import net.kalish.hologram.service.model.Transaction;
import net.kalish.hologram.service.model.TransactionLog2;
import org.msgpack.MessagePack;
import org.msgpack.unpacker.BufferUnpacker;
import org.msgpack.unpacker.Unpacker;

/**
 * Created by kris on 2/24/16.
 */
public class ServerToLog implements HListener {

    private MessagePack mp;
    private TransactionLog2 log;

    public ServerToLog(TransactionLog2 log) {
        this.mp = new MessagePack();
        this.log = log;
    }

    @Override
    public void handleMessage(ServiceMessage msg) {
        try {
            Transaction t = mp.read(msg.serializedTransaction, Transaction.class);
            log.append(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
