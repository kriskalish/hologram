package net.kalish.hologram.client;

/**
 * a little main class just to play with things
 */
public class MainTest {
    public static void main(String args[]) {
        TcpConnector tc = new TcpConnector("127.0.0.1", 8989);

        try {
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

        } catch (Exception e) {
            System.err.println(e);
        }

    }
}
