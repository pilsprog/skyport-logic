package skyport.network.ai;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.Player;
import skyport.game.Vector;
import skyport.message.Message;
import skyport.message.action.ActionMessage;
import skyport.message.action.HandshakeMessage;
import skyport.message.action.LoadoutMessage;
import skyport.network.Connection;

public class AIConnection extends Connection {
    private volatile Player player;
    public boolean hasToPass = false;
    public boolean loaded = false;

    private final Logger logger = LoggerFactory.getLogger(AIConnection.class);

    public AIConnection(Socket socket) {
        super(socket);
        this.player = new Player();

    }

    @Override
    public void run() {
        super.run();
        for (;;) {
            try {
                Message message = gson.fromJson(this.readLine(), Message.class);
                parseLoadout(message);
                this.loaded = true;
                break;
            } catch (ProtocolException e) {
                this.sendError(e.getMessage());
            } catch (IOException e) {
                logger.warn("Disconnect from " + this.getClass().getSimpleName() + ":" + this.getIP() + ".");
                this.close();
                return;
            }
        }
        this.parseActions();
    }

    public boolean gotLoadout() {
        return this.loaded;
    }

    @Override
    protected void input(Message message) throws IOException, ProtocolException {
        if (message instanceof ActionMessage) {
            messages.add((ActionMessage)message);
        } else {
            throw new ProtocolException("Unexpected message, got '" + message.getMessage() + "' but expected 'action'.");
        }
    }

    private void parseLoadout(Message message) throws ProtocolException {
        if (!(message instanceof LoadoutMessage)) {
            throw new ProtocolException("Expected 'loadout', but got '" + message.getMessage() + "'.");
        }
        
        LoadoutMessage loadout = (LoadoutMessage)message;
        loadout.performAction(player, null);
    }

    protected void parseHandshake(Message m) throws ProtocolException {
        if (!(m instanceof HandshakeMessage)) {
            throw new ProtocolException("Expected 'connect' handshake, but got '" + m.getMessage());
        }
        
        HandshakeMessage message = (HandshakeMessage)m;
        message.performAction(player, null);
        this.identifier = player.getName();
    }

    public void setSpawnpoint(Vector spawnpoint) {
        logger.info("Player '" + player.getName() + "' spawns at " + spawnpoint.getString() + ".");
        player.setPosition(spawnpoint);
        player.setSpawn(spawnpoint);
    }

    public void clearAllMessages() {
        if (messages.size() > 0) {
            logger.warn("Message inbox of " + player.getName() + " contained " + messages.size() + " extra messages, discarding...");
        }
        messages.clear();
    }

    public void invalidAction(String type) {
        this.sendError("Invalid action: " + type + ".");
    }

    public void givePenality(int points) {
        logger.warn(player.getName() + " got " + points + " penality.");
        player.score -= points;
    }

    public Player getPlayer() {
        return player;
    }
}
