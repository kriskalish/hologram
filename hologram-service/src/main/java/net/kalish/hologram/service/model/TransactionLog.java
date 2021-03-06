package net.kalish.hologram.service.model;

import net.kalish.hologram.service.model.Transaction;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Scaffold for a transaction log of a single collection
 * https://github.com/LMAX-Exchange/disruptor/issues/30
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
        //log.take();
    }

    public Transaction take() throws InterruptedException {
        return log.take();
    }

    public void clear() {
        log.clear();
    }

    public long getNextId() {
        return currentId.incrementAndGet();
    }
}
