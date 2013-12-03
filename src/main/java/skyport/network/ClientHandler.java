package skyport.network;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;


public class ClientHandler implements Runnable {
    private Connection connection;
    
    private final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    
    public ClientHandler(Connection connection) {
        this.connection = connection;
        logger.info("Got new " + connection.getClass().getSimpleName());
    }
    
    @Override
    public void run() {
        for(;;) {
            try {
                String json = connection.readLine();
                if (json == null) {
                    throw new IOException("Client disconnected");
                }
                connection.input(json);
            } catch (IOException e) {
                logger.warn("Disconnect from " + connection.getClass().getSimpleName() + connection.getIP());
                connection.close();
                return;
            } catch (ProtocolException e) {
                connection.sendError(e.getMessage());
            }
        }
    }   
}