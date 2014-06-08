package skyport.network.ai;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.Player;
import skyport.game.Tile;
import skyport.game.Util;
import skyport.game.weapon.Weapon;
import skyport.message.HandshakeMessage;
import skyport.message.LoadoutMessage;
import skyport.message.action.ActionMessage;
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
                String json = this.readLine();
                parseLoadout(json);
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
    protected void input(String json) throws IOException, ProtocolException {
        ActionMessage actionMessage = gson.fromJson(json, ActionMessage.class);
        String message = actionMessage.getMessage();
        if (message == null) {
            throw new ProtocolException("Unexpected packet: '" + json + "'.");
        }
        if (message.equals("action")) {
            synchronized (messages) {
                messages.add(actionMessage);
            }
        } else {
            throw new ProtocolException("Unexpected message, got '" + message + "' but expected 'action'.");
        }
    }

    private void parseLoadout(String json) throws ProtocolException {
        LoadoutMessage loadout = gson.fromJson(json, LoadoutMessage.class);
        String message = loadout.getMessage();
        if (!message.equals("loadout")) {
            throw new ProtocolException("Expected 'loadout', but got '" + message + "' key.");
        }
        Weapon primary = loadout.getPrimaryWeapon();
        Weapon secondary = loadout.getSecondaryWeapon();

        if (!Util.validateWeapon(primary)) {
            throw new ProtocolException("Invalid primary weapon: '" + primary.getName() + "'.");
        }
        if (!Util.validateWeapon(secondary)) {
            throw new ProtocolException("Invalid secondary weapon: '" + secondary.getName() + "'.");
        }
        if (primary.equals(secondary)) {
            throw new ProtocolException("Invalid loadout: Can't have the same weapon twice.");
        }

        player.setLoadout(primary, secondary);

        logger.info(player.getName() + " selected loadout: " + player.primaryWeapon.getName() + " and " + player.secondaryWeapon.getName() + ".");

        synchronized (this) {
            gotLoadout = true;
        }
    }

    protected boolean parseHandshake(String json) throws ProtocolException {
        HandshakeMessage message = gson.fromJson(json, HandshakeMessage.class);
        String m = message.getMessage();
        if (!m.equals("connect")) {
            throw new ProtocolException("Expected 'connect' handshake, but got '" + m + "' key.");
        }
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
        player.position = spawnpoint;
        player.setSpawn(spawnpoint);
        player.position.playerOnTile = this.player;
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
        player.position.playerOnTile = null;
        player.position = player.getSpawn();
        player.position.playerOnTile = this.player;
        player.health = 100;
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
