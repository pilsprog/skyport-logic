package skyport.network.ai;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;

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
                String json = read();
                if (json == null) {
                    throw new IOException("Client disconnected");
                }
                connection.input(json);
            } catch (IOException e) {
                // globalClientList.remove(connection);
                Debug.warn("Disconnect from " + connection.getIP() + ". " + globalClientList.size() + " clients active.");
                connection.close();
                return;
            } catch (ProtocolException e) {
                connection.sendError(e.getMessage());
            }
        }
    }

    public synchronized String read() throws IOException {
        return connection.readLine();        
    }
}
