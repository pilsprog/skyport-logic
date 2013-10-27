package skyport.network.graphics;

import java.io.IOException;

import skyport.debug.Debug;
import skyport.exception.ProtocolException;

public class GraphicsClientHandler implements Runnable {
    GraphicsConnection connection;

    public GraphicsClientHandler(GraphicsConnection gConnection) {
        connection = gConnection;
        Debug.debug("Created new reader thread.");
    }

    @Override
    public void run() {
        while (true) {
            try {
                String o = read();
                if (o == null) {
                    throw new IOException("Graphics disconnected");
                }
                connection.input(o);
            } catch (IOException e) {
                Debug.error("GUI disconnected, exiting!");
            } catch (ProtocolException e) {
                connection.sendError(e.getMessage());
            }
        }
    }

    public synchronized String read() throws IOException {
        return connection.readLine();
    }
}
