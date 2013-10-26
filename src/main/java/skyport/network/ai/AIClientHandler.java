package skyport.network.ai;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONException;
import org.json.JSONObject;

import skyport.debug.Debug;
import skyport.exception.ProtocolException;

public class AIClientHandler implements Runnable {
    AIConnection connection;
    ConcurrentLinkedQueue<AIConnection> globalClientList;

    public AIClientHandler(AIConnection aiConnection, ConcurrentLinkedQueue<AIConnection> lq) {
        connection = aiConnection;
        globalClientList = lq;
        Debug.info("Got new AI client. " + lq.size() + " clients are now active.");
    }

    @Override
    public void run() {
        while (true) {
            try {
                JSONObject o = read();
                if (o == null) {
                    throw new IOException("Client disconnected");
                }
                connection.input(o);
            } catch (IOException e) {
                // globalClientList.remove(connection);
                Debug.warn("Disconnect from " + connection.getIP() + ". " + globalClientList.size() + " clients active.");
                connection.isAlive = false;
                return;
            } catch (ProtocolException e) {
                try {
                    JSONObject errorMessage = new JSONObject().put("error", e.getMessage());
                    connection.sendMessage(errorMessage);
                } catch (JSONException f) {
                }
            }
        }
    }

    public synchronized JSONObject read() throws IOException, ProtocolException {
        String line = connection.readLine();
        JSONObject obj = null;
        if (line == null) {
            return null;
        }
        try {
            obj = new JSONObject(line);
            return obj;
        } catch (JSONException e) {
            throw new ProtocolException("Invalid packet received: " + e.getMessage());
        }
    }
}
