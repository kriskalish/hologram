package net.kalish.hologram.service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Scaffold for a transaction log of a single collection
 */
public class TransactionLog {
    public static int logCapacity = 1000000; // 1 million

    protected AtomicLong currentId;
    protected ArrayBlockingQueue<Transaction> log;
    protected String name;

    public TransactionLog() {
        name = "test";
        currentId = new AtomicLong(0);
        log = new ArrayBlockingQueue<Transaction>(logCapacity);

    }

    public void append(Transaction t) throws InterruptedException {
        log.put(t);
    }

    public long getNextId() {
        return currentId.incrementAndGet();
    }
}
