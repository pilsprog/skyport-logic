package skyport.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skyport.network.ai.AIClientHandler;
import skyport.network.ai.AIConnection;
import skyport.network.graphics.GraphicsClientHandler;
import skyport.network.graphics.GraphicsConnection;
import skyport.network.graphics.GraphicsContainer;

public class Acceptor implements Runnable {
    private ServerSocket acceptorSocket;
    private ConcurrentLinkedQueue<AIConnection> globalClientList;
    private int clients;
    private int clientsAccepted = 0;
    private boolean isGraphicsManager = false;
    public GraphicsContainer graphics;
    
    private final Logger logger = LoggerFactory.getLogger(Acceptor.class);

    public Acceptor(int port, ConcurrentLinkedQueue<AIConnection> globalClients, int clients, boolean isGraphicsManager, GraphicsContainer graphicsContainer) {
        try {
            this.acceptorSocket = new ServerSocket(port);
            this.acceptorSocket.setReuseAddress(true);
            this.graphics = graphicsContainer;
            this.clients = clients;
            this.globalClientList = globalClients;
            this.isGraphicsManager = isGraphicsManager;
            if (this.isGraphicsManager) {
                logger.info("listening on port " + acceptorSocket.toString() + " for the GUI");
            } else {
                logger.info("listening on port " + acceptorSocket.toString() + " for players");
            }
        } catch (IOException e) {
            logger.error("Error binding to port: " + e);
        }
    }

    @Override
    public void run() {
        logger.debug("Starting to accept incoming connections");
        while (clientsAccepted < clients) {
            try {
                Socket clientSocket = acceptorSocket.accept();
                logger.debug("Connect from " + clientSocket);
                if (!isGraphicsManager) {
                    spawnReadHandlerThread(clientSocket);
                } else {
                    spawnGraphicsHandlerThread(clientSocket);
                }
                clientsAccepted++;
            } catch (IOException e) {
                logger.warn("Error accepting connection: " + e);
            }
        }
        logger.debug("Accepted " + clients + " clients, exiting");
        try {
            acceptorSocket.close();
        } catch (IOException e) {
        }
    };

    public GraphicsConnection spawnGraphicsHandlerThread(Socket clientSocket) {
        logger.debug("Spawning graphics handler!");
        GraphicsConnection conn = new GraphicsConnection(clientSocket, graphics);
        GraphicsClientHandler handler = new GraphicsClientHandler(conn);
        Thread thread = new Thread(handler);
        thread.start();
        return conn;
    }

    public void spawnReadHandlerThread(Socket clientSocket) {
        AIConnection aiConnectionObject = new AIConnection(clientSocket);
        globalClientList.add(aiConnectionObject);
        AIClientHandler handler = new AIClientHandler(aiConnectionObject, globalClientList);
        Thread thread = new Thread(handler);
        thread.start();
    }
}
