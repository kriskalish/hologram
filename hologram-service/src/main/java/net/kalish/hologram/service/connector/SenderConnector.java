package net.kalish.hologram.service.connector;


import net.kalish.hologram.service.model.ServiceMessage;

public interface SenderConnector {
    void pushToSlave(ServiceMessage m) throws InterruptedException;
}
