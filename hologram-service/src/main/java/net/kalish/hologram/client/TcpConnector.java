package net.kalish.hologram.client;

import it.unimi.dsi.fastutil.io.FastBufferedOutputStream;
import net.kalish.hologram.service.model.OperationType;
import net.kalish.hologram.service.model.Transaction;
import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;

import java.net.Socket;


/**
 * First version of tcp client connector just to get things off the ground
 */
public class TcpConnector {
    // todo, need a collection of hosts/ports and a connection pool
    private String serverHost;
    private int serverPort;

    private Socket sock;
    //ObjectOutputStream oos;
    //Output o; Kryo k;
    MessagePack mp = new MessagePack(); Packer p;

    public TcpConnector(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;

        //k = new Kryo();
    }

    protected void sendToMaster(Transaction t) throws Exception {
        if(sock == null || !sock.isConnected()) {
            sock = new Socket(serverHost, serverPort);
            //oos = new ObjectOutputStream(sock.getOutputStream());
            //o = new Output(new BufferedOutputStream(sock.getOutputStream()));

            p = mp.createPacker(new FastBufferedOutputStream(sock.getOutputStream()));
        }


        p.write(t);
        //oos.writeObject(t);
        //k.writeObject(o, t);
        p.
        p.write(t);
    }

    public void put(String key, String value) throws Exception {
        Transaction t = new Transaction(OperationType.MapPut, key, value);
        sendToMaster(t);
    }


}
