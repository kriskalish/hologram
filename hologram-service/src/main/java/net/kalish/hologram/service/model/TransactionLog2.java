package net.kalish.hologram.service.model;

import com.lmax.disruptor.*;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.util.DaemonThreadFactory;
import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import net.kalish.hologram.service.LazyConfig;
import net.kalish.hologram.service.util.HListener;
import org.msgpack.MessagePack;

import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Scaffold for a transaction log of a single collection
 * https://github.com/LMAX-Exchange/disruptor/issues/30
 */
// https://github.com/LMAX-Exchange/disruptor/blob/master/src/test/java/com/lmax/disruptor/example/DynamiclyAddHandler.java#L31
public class TransactionLog2 extends  TransactionLog {

    protected AtomicLong currentId;

    protected String name;

    protected Disruptor<Transaction> disruptor;
    protected RingBuffer<Transaction> ringBuffer;
    protected List<BatchEventProcessor<Transaction>> processors;
    protected List<DynamicHandler> handlers;
    protected ExecutorService executor;

    public TransactionLog2() {
        name = "test";
        currentId = new AtomicLong(0);

        executor = Executors.newCachedThreadPool(DaemonThreadFactory.INSTANCE);
        disruptor = new Disruptor<>(Transaction::new, 1024, executor);

        ringBuffer = disruptor.start();

        processors = new ArrayList<>();
        handlers = new ArrayList<>();
    }

    @Override
    public void append(Transaction t) throws InterruptedException {
        // todo this probably makes a ton of gc overhead if it runs for a while, but we want to get a benchmark done to get an idea of what's going on
        ringBuffer.publishEvent((Transaction toFill, long l) -> {toFill.id = t.id; toFill.key = t.key; toFill.operation = t.operation; toFill.value = t.value;});
    }

    @Override
    public Transaction take() throws InterruptedException {
        return null;
    }

    @Override
    public long getNextId() {
        return currentId.incrementAndGet();
    }

    public void addListener(DynamicHandler dh) {
        BatchEventProcessor<Transaction> processor;

        if(processors.size() == 0)
            processor = new BatchEventProcessor<>(ringBuffer, ringBuffer.newBarrier(), dh);
        else
            processor = new BatchEventProcessor<>(ringBuffer, ringBuffer.newBarrier(processors.get(0).getSequence()), dh);


        ringBuffer.addGatingSequences(processor.getSequence());

        executor.execute(processor);

        processors.add(processor);
        handlers.add(dh);
    }

    public void removeListener(DynamicHandler dh) throws Exception {
        int idx = handlers.indexOf(dh);

        BatchEventProcessor<Transaction> processor = processors.get(idx);
        //DynamicHandler dh = handlers.get(idx);


        processor.halt();
        // Wait for shutdown the complete
        dh.awaitShutdown();
        // Remove the gating sequence from the ring buffer
        ringBuffer.removeGatingSequence(processor.getSequence());

        handlers.remove(idx);
        processors.remove(idx);
    }



    public static class DynamicHandler implements EventHandler<Transaction>, LifecycleAware
    {
        private final CountDownLatch shutdownLatch = new CountDownLatch(1);

        private int masterToSlavePort = LazyConfig.MASTER_TO_SLAVE_PORT;
        private ServerSocket sock;
        private OutputStream os;
        private MessagePack mp;

        @Override
        public void onEvent(final Transaction event, final long sequence, final boolean endOfBatch) throws Exception
        {
            byte[] bytes = mp.write(event);
            os.write(bytes);
        }

        @Override
        public void onStart()
        {
            System.out.println("starting...");


            try {
                if (sock == null || !sock.isBound()) {
                    System.out.println("Creating new server socket...");
                    sock = new ServerSocket(masterToSlavePort);
                }

                Socket s = sock.accept();
                os = new FastBufferedOutputStream(s.getOutputStream());
                mp = new MessagePack();

            } catch (Exception e ) {
                e.printStackTrace();
            }

        }

        @Override
        public void onShutdown()
        {
            shutdownLatch.countDown();
        }

        public void awaitShutdown() throws InterruptedException
        {
            shutdownLatch.await();
        }
    }
}
