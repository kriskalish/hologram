package net.kalish.hologram.service.connector;


import net.kalish.hologram.service.model.CrossServiceMessage;

public interface SenderConnector {
    void pushToSlave(CrossServiceMessage m) throws InterruptedException;
}
