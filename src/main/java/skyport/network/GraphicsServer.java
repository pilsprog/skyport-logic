package skyport.network;

import java.net.InetSocketAddress;
import java.util.Collection;

import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.adapter.ActionMessageDeserializer;
import skyport.adapter.MessageDeserializer;
import skyport.adapter.TileSerializer;
import skyport.adapter.Vector2dAdapter;
import skyport.game.Tile;
import skyport.game.Vector2d;
import skyport.message.ErrorMessage;
import skyport.message.Message;
import skyport.message.action.ActionMessage;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class GraphicsServer extends WebSocketServer {
    
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private Gson gson = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
        .registerTypeAdapter(Vector2d.class, new Vector2dAdapter())
        .registerTypeAdapter(Message.class, new MessageDeserializer())
        .registerTypeAdapter(ActionMessage.class, new ActionMessageDeserializer())
        .registerTypeAdapter(Tile.class, new TileSerializer())
        .create();
    
    private final Message info;
    
    public GraphicsServer(Message info, int port) {
        super(new InetSocketAddress(port));
        this.info = info;
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        logger.info("New graphics client connected: " + conn.getRemoteSocketAddress());
        String json = gson.toJson(info);
        conn.send(json);
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        logger.info("Graphics client left:  " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        final ErrorMessage error = new ErrorMessage("Messages are not allowed.");
        String json = gson.toJson(error);
        conn.send(json);
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        logger.error("Graphics client error: "+ conn.getRemoteSocketAddress(), ex);
    }
    
    public void sendToAll(Message message) {   
        String json = gson.toJson(message);
        logger.info("Sending message: "+ json);
        Collection<WebSocket> connections = this.connections();
        synchronized(connections) {
            for(WebSocket conn : connections) {
                conn.send(json);
            }
        }
    }
}
