package skyport.network.graphics;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GraphicsAcceptor implements Runnable {
    private ServerSocket socket;
    private GraphicsConnection connection;

    private final Logger logger = LoggerFactory.getLogger(GraphicsAcceptor.class);

    public GraphicsAcceptor(int port) throws IOException {
        this.socket = new ServerSocket(port);
    }

    @Override
    public void run() {
        logger.info("Waiting for graphics connection.");
        while (true) {
            try {
                Socket client = socket.accept();
                this.connection = new GraphicsConnection(client);
                logger.info("Graphics client connected.");
                new Thread(connection, "Graphics").start();
                return;
            } catch (IOException e) {
                logger.warn("Error on accepting graphics connection.", e);
            }
        }
    }

    synchronized public GraphicsConnection getConnection() {
        return connection;
    }
}
