package skyport.network.ai;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import skyport.debug.Debug;
import skyport.exception.ProtocolException;
import skyport.game.Coordinate;
import skyport.game.Player;
import skyport.game.Tile;
import skyport.game.TileType;
import skyport.game.Util;
import skyport.game.weapon.Droid;
import skyport.game.weapon.Laser;
import skyport.game.weapon.Mortar;
import skyport.game.weapon.Weapon;
import skyport.message.ErrorMessage;
import skyport.message.HandshakeMessage;
import skyport.message.LoadoutMessage;
import skyport.message.Message;
import skyport.message.StatusMessage;
import skyport.network.Connection;
import skyport.network.graphics.GraphicsConnection;

public class AIConnection extends Connection {
    private Player player;
    public AtomicBoolean gotLoadout = new AtomicBoolean(false);
    public boolean hasToPass = false;
    public boolean needsRespawn = false;

    public AIConnection(Socket socket) {
        super(socket);
    }

    public void sendError(String errorString) {
        Message error = new ErrorMessage(errorString);
        sendMessage(error);
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
            Message message = gson.fromJson(json, Message.class);
            if (message.getMessage() != null) {
                String m = message.getMessage();
                if (m.equals("action")) {
                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(json);
                    } catch (JSONException e) {
                        throw new ProtocolException("Invalid packet received: " + e.getMessage());
                    }
                    messages.add(obj);
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

    @Override
    public void sendMessage(JSONObject o) {
        if (!isAlive) {
            Debug.debug("player '" + this.player.name + "' disconnected, not sending...");
            return;
        }
        super.sendMessage(o);
    }

    public synchronized void setSpawnpoint(Tile spawnpoint) {
        Debug.info("Player '" + player.name + "' spawns at " + spawnpoint.coords.getString());
        player.position = spawnpoint;
        player.spawnTile = spawnpoint;
        player.position.playerOnTile = this;
    }

    public void clearAllMessages() {
        if (messages.size() > 0) {
            Debug.warn("Message inbox of " + player.name + " contained " + messages.size() + " extra messages, discarding...");
        }
        messages.clear();
    }

    public synchronized boolean doMove(JSONObject o) {
        // TODO verify that this is all exactly right
        try {
            String direction = o.getString("direction");
            if (direction.equals("up")) {
                if (player.position.up != null && player.position.up.isAccessible()) {
                    if (player.position.up.playerOnTile != null) {
                        throw new ProtocolException("Player " + player.position.up.playerOnTile.player.getName() + " is already on this tile.");
                    }
                    player.position.playerOnTile = null;
                    player.position = player.position.up;
                    player.position.playerOnTile = this;
                    return true;
                } else {
                    throw Util.throwInaccessibleTileException("up", player.position.up);
                }
            } else if (direction.equals("down")) {
                if (player.position.down != null && player.position.down.isAccessible()) {
                    if (player.position.down.playerOnTile != null) {
                        throw new ProtocolException("Player " + player.position.down.playerOnTile.player.getName() + " is already on this tile.");
                    }
                    player.position.playerOnTile = null;
                    player.position = player.position.down;
                    player.position.playerOnTile = this;
                    return true;
                } else {
                    throw Util.throwInaccessibleTileException("down", player.position.down);
                }
            } else if (direction.equals("left-down")) {
                if (player.position.leftDown != null && player.position.leftDown.isAccessible()) {
                    if (player.position.leftDown.playerOnTile != null) {
                        throw new ProtocolException("Player " + player.position.leftDown.playerOnTile.player.getName() + " is already on this tile.");
                    }
                    player.position.playerOnTile = null;
                    player.position = player.position.leftDown;
                    player.position.playerOnTile = this;
                    return true;
                } else {
                    throw Util.throwInaccessibleTileException("left-down", player.position.leftDown);
                }
            } else if (direction.equals("left-up")) {
                if (player.position.leftUp != null && player.position.leftUp.isAccessible()) {
                    if (player.position.leftUp.playerOnTile != null) {
                        throw new ProtocolException("Player " + player.position.leftUp.playerOnTile.player.getName() + " is already on this tile.");
                    }
                    player.position.playerOnTile = null;
                    player.position = player.position.leftUp;
                    player.position.playerOnTile = this;
                    return true;
                } else {
                    throw Util.throwInaccessibleTileException("left-up", player.position.leftUp);
                }
            } else if (direction.equals("right-down")) {
                if (player.position.rightDown != null && player.position.rightDown.isAccessible()) {
                    if (player.position.rightDown.playerOnTile != null) {
                        throw new ProtocolException("Player " + player.position.rightDown.playerOnTile.player.getName() + " is already on this tile.");
                    }
                    player.position.playerOnTile = null;
                    player.position = player.position.rightDown;
                    player.position.playerOnTile = this;
                    return true;
                } else {
                    throw Util.throwInaccessibleTileException("right-down", player.position.rightDown);
                }
            } else if (direction.equals("right-up")) {
                if (player.position.rightUp != null && player.position.rightUp.isAccessible()) {
                    if (player.position.rightUp.playerOnTile != null) {
                        throw new ProtocolException("Player " + player.position.rightUp.playerOnTile.player.getName() + " is already on this tile.");
                    }
                    player.position.playerOnTile = null;
                    player.position = player.position.rightUp;
                    player.position.playerOnTile = this;
                    return true;
                } else {
                    throw Util.throwInaccessibleTileException("right-up", player.position.rightUp);
                }
            } else {
                throw new ProtocolException("Invalid direction: '" + direction + "'");
            }
        } catch (JSONException | ProtocolException e) {
            this.sendError("Invalid move packet: " + e.getMessage());
            return false;
        }
    }

    public void invalidAction(String type) {
        this.sendError("Invalid action: " + type);
    }

    public boolean shootLaser(JSONObject action, GraphicsConnection graphicsConnection, int turnsLeft) {
        int laserLevel = 1;
        if (player.primaryWeapon.getName().equals("laser")) {
            laserLevel = player.primaryWeapon.getLevel();
        } else if (player.secondaryWeapon.getName().equals("laser")) {
            laserLevel = player.secondaryWeapon.getLevel();
        } else {
            return false;
        }
        try {
            String direction = action.getString("direction");
            Laser laser = new Laser(this, turnsLeft);
            if (laser.setDirection(direction)) {
                laser.setPosition(player.position);
                Coordinate endvector = laser.performShot(laserLevel);
                graphicsConnection.setStartStopHack(player.position.coords, endvector);
            } else {
                sendError("Invalid shot: unknown direction '" + direction + "'");
                return false;
            }
            return true;
        } catch (JSONException e) {
            sendError("Invalid shot: lacks a direction key");
        }
        return false;
    }

    public boolean shootDroid(JSONObject action, int turnsLeft) {
        int droidLevel = 1; // TODO
        if (player.primaryWeapon.getName().equals("droid")) {
            droidLevel = player.primaryWeapon.getLevel();
        } else if (player.secondaryWeapon.getName().equals("droid")) {
            droidLevel = player.secondaryWeapon.getLevel();
        } else {
            Debug.warn("User '" + player.name + "' attempted to shoot the droid, but doesn't have it");
            return false;
        }
        int range = 3;
        if (droidLevel == 2) {
            range = 4;
        }
        if (droidLevel == 3) {
            range = 5;
        } // replicated here for more friendly error messages
        try {
            JSONArray directionSequence = action.getJSONArray("sequence");
            Droid droid = new Droid(this, turnsLeft);
            if (directionSequence.length() > range) {
                Debug.warn("Got " + directionSequence.length() + " commands for the droid, but your droids level (" + droidLevel + ") only supports " + range + " steps.");
                sendError("Got " + directionSequence.length() + " commands for the droid, but your droids level (" + droidLevel + ") only supports " + range + " steps.");
            }
            if (droid.setDirections(directionSequence, droidLevel)) {
                droid.setPosition(player.position);
                int stepsTaken = droid.performShot();
                JSONArray truncatedArray = new JSONArray();
                for (int i = 0; i < stepsTaken; i++) {
                    truncatedArray.put(directionSequence.get(i));
                }
                action.put("sequence", truncatedArray);
                Debug.debug("droid steps taken: " + stepsTaken);
            } else {
                sendError("Invalid shot: unknown direction in droid sequence");
                return false;
            }
            return true;
        } catch (JSONException e) {
            sendError("Invalid shot: lacks a direction key");
        }
        return false;
    }

    public boolean shootMortar(JSONObject action, int turnsLeft) {
        int mortarLevel = 1;
        if (player.primaryWeapon.getName().equals("mortar")) {
            mortarLevel = player.primaryWeapon.getLevel();
        } else if (player.secondaryWeapon.getName().equals("mortar")) {
            mortarLevel = player.secondaryWeapon.getLevel();
        } else {
            Debug.warn("User '" + player.name + "' attempted to shoot the mortar, but doesn't have it");
            return false;
        }
        try {
            Coordinate relativeTargetCoordinates = new Coordinate(action.getString("coordinates"));
            Mortar mortar = new Mortar(this, turnsLeft);
            mortar.setPosition(this.player.position);
            mortar.setTarget(relativeTargetCoordinates, mortarLevel);
            return mortar.performShot();
        } catch (JSONException e) {
            sendError("Invalid shot: lacks 'coordinates' key");
            return false;
        }
    }

    public boolean mineResource() {
        TileType tileType = this.player.position.tileType;
        Debug.game("Player " + player.name + " mining " + tileType);
        boolean minedResource = this.player.position.mineTile();
        if (minedResource) {
            if (tileType == TileType.RUBIDIUM) {
                player.rubidiumResources++;
            }
            if (tileType == TileType.EXPLOSIUM) {
                player.explosiumResources++;
            }
            if (tileType == TileType.SCRAP) {
                player.scrapResources++;
            }
            Debug.debug("Resources of player " + player.name + " are now: Rubidium: " + player.rubidiumResources + ", Explosium: " + player.explosiumResources + ", Scrap: " + player.scrapResources);
            return true;
        }
        return false;
    }

    public void damagePlayer(int hitpoints, AIConnection dealingPlayer) {
        if (player.health <= 0) {
            Debug.warn("Player is already dead.");
            return;
        }
        Debug.stub("'" + this.player.name + "' received " + hitpoints + " damage from '" + dealingPlayer.player.name + "'!");
        player.health -= hitpoints;
        if (!(dealingPlayer.player.name.equals(this.player.name))) {
            dealingPlayer.givePoints(hitpoints); // damaged user other than
            // self, award points
        }
        if (player.health <= 0) {
            Debug.game(this.player.name + " got killed by " + dealingPlayer.player.name);
            if (!(dealingPlayer.player.name.equals(this.player.name))) {
                dealingPlayer.givePoints(20); // 20 bonus points for killing
                // someone
            }
            player.score -= 40;
            player.health = 0;
            hasToPass = true;
            needsRespawn = true;
        }
    }

    public boolean upgradeWeapon(String weapon) {
        Debug.debug(player.name + " upgrading his " + weapon);
        if (player.primaryWeapon.getName().equals(weapon)) {
            Debug.stub("upgrading primary weapon (" + weapon + ")");
            boolean success = subtractResourcesForWeaponUpgrade(weapon, player.primaryWeapon.getLevel());
            if (success) {
                player.primaryWeapon.upgrade();
                Debug.guiMessage(player.name + " upgrades his " + weapon);
                return true;
            } else {
                return false;
            }
        } else if (player.secondaryWeapon.getName().equals(weapon)) {
            boolean success = subtractResourcesForWeaponUpgrade(weapon, player.secondaryWeapon.getLevel());
            if (success) {
                player.secondaryWeapon.upgrade();
                Debug.guiMessage(player.name + " upgrades his " + weapon);
                return true;
            } else {
                return false;
            }
        } else {
            Debug.warn(player.name + " tried to upgrade weapon '" + weapon + "', but doesn't have it.");
            return false;
        }
    }

    boolean subtractResourcesForWeaponUpgrade(String weapon, int currentLevel) {
        int resourcesToSubtract = 4;
        if (currentLevel == 2) {
            resourcesToSubtract = 5;
        }
        if (currentLevel == 3) {
            Debug.warn(player.name + " tried to upgrade his " + weapon + ", but it is already level 3.");
            return false;
        }
        if (weapon.equals("laser")) {
            if (player.rubidiumResources >= resourcesToSubtract) {
                player.rubidiumResources -= resourcesToSubtract;
                return true;
            } else {
                Debug.warn("Tried to upgrade the laser, but not enough rubidium");
                return false;
            }
        }
        if (weapon.equals("mortar")) {
            if (player.explosiumResources >= resourcesToSubtract) {
                player.explosiumResources -= resourcesToSubtract;
                return true;
            } else {
                Debug.warn("Tried to upgrade the mortar, but not enough explosium");
                return false;
            }
        }
        if (weapon.equals("droid")) {
            if (player.scrapResources >= resourcesToSubtract) {
                player.scrapResources -= resourcesToSubtract;
                return true;
            } else {
                Debug.warn("Tried to upgrade the droid, but not enough scrap");
                return false;
            }
        }
        return false;
    }

    public void respawn() {
        player.position.playerOnTile = null;
        player.position = player.spawnTile;
        player.position.playerOnTile = this;
        player.health = 100;
        needsRespawn = false;
    }

    void givePoints(int points) {
        Debug.info("got awarded " + points + " points");
        player.score += points;
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
