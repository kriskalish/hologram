package net.kalish.hologram.service.model;

/**
 *
 */
public class ServiceMessage {
    public long transactionId;
    public byte[] serializedTransaction;

    public ServiceMessage(long transactionId, byte[] serializedTransaction) {
        this.transactionId = transactionId;
        this.serializedTransaction = serializedTransaction;
    }
}
