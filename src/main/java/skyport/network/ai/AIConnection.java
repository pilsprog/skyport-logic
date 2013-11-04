package skyport.network.ai;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import skyport.debug.Debug;
import skyport.exception.ProtocolException;
import skyport.game.Player;
import skyport.game.Tile;
import skyport.game.Util;
import skyport.game.weapon.Weapon;
import skyport.message.HandshakeMessage;
import skyport.message.LoadoutMessage;
import skyport.message.Message;
import skyport.message.StatusMessage;
import skyport.message.action.ActionMessage;
import skyport.network.Connection;

public class AIConnection extends Connection {
    private Player player;
    public AtomicBoolean gotLoadout = new AtomicBoolean(false);
    public boolean hasToPass = false;
    public boolean needsRespawn = false;

    public AIConnection(Socket socket) {
        super(socket);
    }

    public synchronized void input(String json) throws IOException, ProtocolException {
        if (!gotHandshake) {
            if (parseHandshake(json)) {
                Message success = new StatusMessage(true);
                this.sendMessage(success);
            }
            return;
        } else if (!gotLoadout.get()) {
            parseLoadout(json);
        } else {
            ActionMessage message = gson.fromJson(json, ActionMessage.class);
            if (message.getMessage() != null) {
                String m = message.getMessage();
                if (m.equals("action")) {
                    messages.add(message);
                } else {
                    throw new ProtocolException("Unexpected message, got '" + m + "' but expected 'action'");
                }
            } else {
                throw new ProtocolException("Unexpected packet: '" + json + "'");
            }
        }
    }

    private void parseLoadout(String json) throws ProtocolException {
        LoadoutMessage loadout = gson.fromJson(json, LoadoutMessage.class);
        String message = loadout.getMessage();
        if (!message.equals("loadout")) {
            throw new ProtocolException("Expected 'loadout', but got '" + message + "' key");
        }
        Weapon primary = loadout.getPrimaryWeapon();
        Weapon secondary = loadout.getSecondaryWeapon();

        if (!Util.validateWeapon(primary)) {
            throw new ProtocolException("Invalid primary weapon: '" + primary.getName() + "'");
        }
        if (!Util.validateWeapon(secondary)) {
            throw new ProtocolException("Invalid secondary weapon: '" + secondary.getName() + "'");
        }
        if (primary.equals(secondary)) {
            throw new ProtocolException("Invalid loadout: Can't have the same weapon twice.");
        }

        player.setLoadout(primary, secondary);

        Debug.info(player.name + " selected loadout: " + player.primaryWeapon.getName() + " and " + player.secondaryWeapon.getName() + ".");
        gotLoadout.set(true);
    }

    private boolean parseHandshake(String json) throws ProtocolException {
        HandshakeMessage message = gson.fromJson(json, HandshakeMessage.class);
        String m = message.getMessage();
        if (!m.equals("connect")) {
            throw new ProtocolException("Expected 'connect' handshake, but got '" + m + "' key");
        }
        int revision = message.getRevision();
        if (revision != 1) {
            throw new ProtocolException("Wrong protocol revision: supporting 1, but got " + revision);
        }
        String name = message.getName();
        Util.validateUsername(name);
        this.player = new Player(name);
        this.identifier = player.name;
        gotHandshake = true;
        return true;
    }

    public synchronized void setSpawnpoint(Tile spawnpoint) {
        Debug.info("Player '" + player.name + "' spawns at " + spawnpoint.coords.getString());
        player.position = spawnpoint;
        player.spawnTile = spawnpoint;
        player.position.playerOnTile = this.player;
    }

    public void clearAllMessages() {
        if (messages.size() > 0) {
            Debug.warn("Message inbox of " + player.name + " contained " + messages.size() + " extra messages, discarding...");
        }
        messages.clear();
    }

    public void invalidAction(String type) {
        this.sendError("Invalid action: " + type);
    }

    public void respawn() {
        player.position.playerOnTile = null;
        player.position = player.spawnTile;
        player.position.playerOnTile = this.player;
        player.health = 100;
        needsRespawn = false;
    }

    public void givePenality(int points) {
        Debug.warn(player.name + " got " + points + " penality");
        player.score -= points;
    }
    
    public Player getPlayer() {
        return player;
    }

    public void printStats() {
        System.out.println(player.name +
                ": HP: " + player.health + 
                ", score: " + player.score +
                ", RUB:" + player.rubidiumResources +
                ", EXP:" + player.explosiumResources +
                ", SCR:" + player.scrapResources +
                ", prim. lvl:" + player.primaryWeapon.getLevel() + 
                ", sec. lvl.:" + player.secondaryWeapon.getLevel());
    }
}
