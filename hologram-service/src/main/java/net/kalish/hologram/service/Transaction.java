package net.kalish.hologram.service;

import org.msgpack.annotation.Message;

import java.io.Serializable;

/**
 * This is a skeleton for a transaction object
 */
@Message
public class Transaction implements Serializable {
    public long id;
    public int operation;
    public String key;
    public String value;

    public Transaction() {}

    public Transaction(long id, int operation, String key, String value) {
        this.id = id;
        this.operation = operation;
        this.key = key;
        this.value = value;
    }

    public Transaction(int operation, String key, String value) {
        this(-1, operation, key, value);
    }

    @Override
    public String toString() {
        return String.format("[ %d, %d, %s, %s ]", id, operation, key, value);
    }
}
