package net.kalish.hologram.service;

import net.kalish.hologram.service.model.TransactionLog;

/**
 * For clearing the transaction log periodically for debugging purposes.
 */
public class Drain implements Runnable {
    private TransactionLog log;

    public Drain(TransactionLog log) {
        this.log = log;
    }

    @Override
    public void run()  {
        while(true) {
            try {
                Thread.sleep(1000 * 5);
                System.out.println("Clearing the transaction log!!!");
                log.clear();

            } catch (Exception e) {
                System.err.println(e);
            }
        }
    }
}
