package skyport.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONObject;

import skyport.debug.Debug;

public abstract class Connection {
    protected Socket socket;
    protected ConcurrentLinkedQueue<JSONObject> messages;
    protected BufferedReader input;
    protected ConcurrentLinkedQueue<JSONObject> messages = new ConcurrentLinkedQueue<JSONObject>();
    public boolean isAlive = true;
    protected boolean gotHandshake = false;
    
    public Connection(Socket socket) {
        this.socket = socket;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            Debug.error("error creating connection handler: " + e);
        }
    }
    
    public String readLine() throws IOException {
        String line = input.readLine();
        return line;
    }
    public String getIP() {
        return socket.getInetAddress() + ":" + Integer.toString(socket.getPort());
    }

}
