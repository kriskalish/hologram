package net.kalish.hologram.client;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import net.kalish.hologram.service.OperationType;
import net.kalish.hologram.service.Transaction;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
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
    Output o;
    Kryo k;

    public TcpConnector(String serverHost, int serverPort) {
        this.serverHost = serverHost;
        this.serverPort = serverPort;

        k = new Kryo();
    }

    protected void sendToMaster(Transaction t) throws Exception {
        if(sock == null || !sock.isConnected()) {
            sock = new Socket(serverHost, serverPort);
            //oos = new ObjectOutputStream(sock.getOutputStream());
            o = new Output(sock.getOutputStream());
        }

        k.writeObject(o, t);

        //oos.writeObject(t);
    }

    public void put(String key, String value) throws Exception {
        Transaction t = new Transaction(OperationType.MapPut, key, value);
        sendToMaster(t);
    }


}
