package net.kalish.hologram.service.model;

/**
 *
 */
public class CrossServiceMessage {
    public long transactionId;
    public byte[] serializedTransaction;

    public CrossServiceMessage(long transactionId, byte[] serializedTransaction) {
        this.transactionId = transactionId;
        this.serializedTransaction = serializedTransaction;
    }
}
