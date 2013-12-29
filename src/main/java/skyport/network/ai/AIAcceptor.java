package skyport.network.ai;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AIAcceptor implements Runnable {
    private ServerSocket socket;
    private int clients;
    private List<AIConnection> connections;

    private final Logger logger = LoggerFactory.getLogger(AIAcceptor.class);

    public AIAcceptor(int port, int clients) throws IOException {
        this.clients = clients;
        this.socket = new ServerSocket(port);
        this.connections = new LinkedList<>();
    }

    @Override
    public void run() {
        logger.info("Waiting for " + clients + " players to connect.");
        for (int i = 0; i < clients; i++) {
            try {
                Socket client = socket.accept();
                AIConnection conn = new AIConnection(client);
                connections.add(conn);
                new Thread(conn).start();
                logger.debug("Player connected from " + client.getInetAddress() + ":" + client.getPort());
            } catch (IOException e) {
                logger.warn("Error on accepting connection.", e);
                i--;
            }
        }
        logger.info("Stop waiting for connections, " + clients + " players has connected.");
    }

    synchronized public List<AIConnection> getConnections() {
        return connections;
    }
}
