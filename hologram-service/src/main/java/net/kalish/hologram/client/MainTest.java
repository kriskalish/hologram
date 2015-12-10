package net.kalish.hologram.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import net.kalish.hologram.service.LazyConfig;
import net.kalish.hologram.service.model.OperationType;
import net.kalish.hologram.service.model.Transaction;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * a little main class just to play with things
 */
public class MainTest {
    public static void main(String args[]) {
        TcpConnector tc = new TcpConnector("127.0.0.1", LazyConfig.DEFAULT_PORT);

        try {
            TestConnector(tc);
            //TestQueue();
            //TestKryoSerialization();
            //TestKryoArraySerialization();
            //TestMessagePackSerialization();

        } catch (Exception e) {
            System.err.println(e);
        }

    }


    public static void TestConnector(TcpConnector tc) throws Exception {
        int numTransactions = 1000000;
        long startTime = System.nanoTime();

        for(int i = 0; i < numTransactions; ++i) {
            tc.put("thisismykey", "thisismyvalue");
            if( i % 10000 == 0)
                System.out.println("Sent " + i + " transactions");
        }

        long elapsedTime = System.nanoTime() - startTime;
        double elapsedTimeSeconds = elapsedTime / 1000000000.0;

        double tps = numTransactions / elapsedTimeSeconds;

        System.out.println("Took " + elapsedTimeSeconds + " seconds. Which is " + tps + " tps.");
    }

    public static void TestQueue() throws Exception {
        int numTransactions = 1000000;
        BlockingQueue<Transaction> bq = new ArrayBlockingQueue<Transaction>(numTransactions);

        long startTime = System.nanoTime();

        for(int i = 0; i < numTransactions; ++i) {
            Transaction t = new Transaction(i, OperationType.MapPut, "thisismykey", "thisismyvalue");
            bq.add(t);

            if( i % 10000 == 0)
                System.out.println("Queued " + i + " transactions");
        }

        long elapsedTime = System.nanoTime() - startTime;
        double elapsedTimeSeconds = elapsedTime / 1000000000.0;

        double tps = numTransactions / elapsedTimeSeconds;

        System.out.println("Took " + elapsedTimeSeconds + " seconds. Which is " + tps + " tps.");
    }

    public static void TestKryoSerialization() throws Exception {
        int numTransactions = 1000000;
        long startTime = System.nanoTime();

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Kryo k = new Kryo();
        Output o = new Output(os);


        for(int i = 0; i < numTransactions; ++i) {
            Transaction t = new Transaction(i, OperationType.MapPut, "thisismykey", "thisismyvalue");

            k.writeObject(o, t);

            if( i % 10000 == 0)
                System.out.println("Queued " + i + " transactions");
        }

        long elapsedTime = System.nanoTime() - startTime;
        double elapsedTimeSeconds = elapsedTime / 1000000000.0;

        double tps = numTransactions / elapsedTimeSeconds;

        System.out.println("Took " + elapsedTimeSeconds + " seconds. Which is " + tps + " tps.");
    }

    public static void TestKryoArraySerialization() throws Exception {
        int numTransactions = 1000000;


        ByteArrayOutputStream os = new ByteArrayOutputStream();
        Kryo k = new Kryo();
        Output o = new Output(os);

        ArrayList<Transaction> list = new ArrayList<Transaction>(numTransactions);
        for(int i = 0; i < numTransactions; ++i) {
            Transaction t = new Transaction(i, OperationType.MapPut, "thisismykey", "thisismyvalue");
            list.add(t);
        }

        long startTime = System.nanoTime();
        k.writeObject(o, list);

        long elapsedTime = System.nanoTime() - startTime;
        double elapsedTimeSeconds = elapsedTime / 1000000000.0;

        double tps = numTransactions / elapsedTimeSeconds;

        System.out.println("Took " + elapsedTimeSeconds + " seconds. Which is " + tps + " tps.");
    }

    public static void TestMessagePackSerialization() throws Exception {
        int numTransactions = 1000000;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        MessagePack mp = new MessagePack();
        Packer p = mp.createPacker(os);



        long startTime = System.nanoTime();


        for(int i = 0; i < numTransactions; ++i) {
            Transaction t = new Transaction(i, OperationType.MapPut, "thisismykey", "thisismyvalue");
            p.write(t);
            //mp.write(os, t);
        }

        long elapsedTime = System.nanoTime() - startTime;
        double elapsedTimeSeconds = elapsedTime / 1000000000.0;

        double tps = numTransactions / elapsedTimeSeconds;

        System.out.println("Took " + elapsedTimeSeconds + " seconds. Which is " + tps + " tps.");

    }
}
