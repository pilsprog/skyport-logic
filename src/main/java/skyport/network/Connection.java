package skyport.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONObject;

public abstract class Connection {
    protected Socket socket;
    protected BufferedReader inputReader;
    protected ConcurrentLinkedQueue<JSONObject> messages;
    public boolean isAlive = true;
    protected boolean gotHandshake = false;
    
    public String readLine() throws IOException {
        String line = inputReader.readLine();
        return line;
    }
    public String getIP() {
        return socket.getInetAddress() + ":" + Integer.toString(socket.getPort());
    }

}
