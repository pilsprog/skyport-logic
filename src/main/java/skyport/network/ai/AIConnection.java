package skyport.network.ai;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.Player;
import skyport.game.Tile;
import skyport.game.Util;
import skyport.message.HandshakeMessage;
import skyport.message.Message;
import skyport.message.action.ActionMessage;
import skyport.message.action.LoadoutMessage;
import skyport.network.Connection;

public class AIConnection extends Connection {
    private volatile Player player;
    private boolean gotLoadout = false;
    public boolean hasToPass = false;
    public boolean needsRespawn = false;

    private final Logger logger = LoggerFactory.getLogger(AIConnection.class);

    public AIConnection(Socket socket) {
        super(socket);
        this.player = new Player();

    }

    @Override
    public void run() {
        super.run();
        while (!gotLoadout) {
            try {
                Message message = gson.fromJson(this.readLine(), Message.class);
                parseLoadout(message);
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
        return this.gotLoadout;
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
        
        synchronized (this) {
            gotLoadout = true;
        }
    }

    protected boolean parseHandshake(Message m) throws ProtocolException {
        if (!(m instanceof HandshakeMessage)) {
            throw new ProtocolException("Expected 'connect' handshake, but got '" + m.getMessage());
        }
        
        HandshakeMessage message = (HandshakeMessage)m;
        int revision = message.getRevision();
        if (revision != 1) {
            throw new ProtocolException("Wrong protocol revision: supporting 1, but got " + revision + ".");
        }
        String name = message.getName();
        Util.validateUsername(name);
        this.player.setName(name);
        this.identifier = player.getName();
        gotHandshake = true;
        return true;
    }

    public void setSpawnpoint(Tile spawnpoint) {
        logger.info("Player '" + player.getName() + "' spawns at " + spawnpoint.coords.getString() + ".");
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

    public void respawn() {
        player.respawn();
        needsRespawn = false;
    }

    public void givePenality(int points) {
        logger.warn(player.getName() + " got " + points + " penality.");
        player.score -= points;
    }

    public Player getPlayer() {
        return player;
    }
}
