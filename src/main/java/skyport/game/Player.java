package skyport.game;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.weapon.Weapon;
import skyport.message.ErrorMessage;
import skyport.message.HandshakeMessage;
import skyport.message.Message;
import skyport.message.StatusMessage;
import skyport.message.action.ActionMessage;
import skyport.message.action.LoadoutMessage;
import skyport.network.Connection;

public class Player {
    private String name;
    private int health = 100;
    private int score = 0;

    private Weapon primaryWeapon;
    private Weapon secondaryWeapon;

    private Vector2d position;
    
    private transient int rubidium = 0;
    private transient int explosium = 0;
    private transient int scrap = 0;

    private transient boolean dead = false;
    private transient Vector2d spawn;
    private transient int turns; 
    private final transient Connection conn;

    private transient final Logger logger = LoggerFactory.getLogger(Player.class);
    
    public Player(Connection conn) {
        this.conn = conn;
    }
    
    public Future<Void> handshake() {
        return CompletableFuture.runAsync(() -> {
            for (;;) {
                Message message = this.conn.next();
                if (message instanceof HandshakeMessage) {
                    HandshakeMessage handshake = (HandshakeMessage) message;
                    try {
                        handshake.validate();
                        this.name = handshake.getName();
                        this.conn.sendMessage(new StatusMessage(true));
                        break;
                    } catch (ProtocolException e) {
                        this.conn.sendMessage(new ErrorMessage(e));
                        continue;
                    }
                }
            }
        });
    }

    public Future<Void> loadout() {
        return CompletableFuture.runAsync(() -> {
            for (;;) {
                Message message = this.conn.next();
                if (message instanceof LoadoutMessage) {
                    LoadoutMessage loadout = (LoadoutMessage) message;
                    try {
                        loadout.validate();
                        this.primaryWeapon = loadout.getPrimaryWeapon();
                        this.secondaryWeapon = loadout.getSecondaryWeapon();
                        break;
                    } catch (ProtocolException e) {
                        this.conn.sendMessage(new ErrorMessage(e));
                        continue;
                    }   
                }
            }  
        });
    }
    
    public String getName() {
        return name;
    }
    
    public void setSpawn(Vector2d spawn) {
        this.spawn = spawn;
        this.position = spawn;
    }
    
    public Vector2d getSpawn() {
        return spawn;
    }

    void givePoints(int points) {
        logger.info("Got awarded " + points + " points.");
        this.score += points;
    }
    
    public int score() {
        return score;
    }

    public void damagePlayer(int hitpoints, Player dealingPlayer) {
        if (this.getHealth() <= 0) {
            logger.warn("Player is already dead.");
            return;
        }
        logger.debug("'" + this.name + "' received " + hitpoints + " damage from '" + dealingPlayer.getName() + "'!");
        this.setHealth(this.getHealth() - (int) Math.round(hitpoints + 0.2 * dealingPlayer.getTurnsLeft() * hitpoints));
        if (!(dealingPlayer.name.equals(this.name))) {
            dealingPlayer.givePoints(hitpoints); // damaged user other than
            // self, award points
        }
        if (this.getHealth() <= 0) {
            logger.info("==> " + this.name + " got killed by " + dealingPlayer.name + ".");
            if (!(dealingPlayer.name.equals(this.name))) {
                dealingPlayer.givePoints(20); // 20 bonus points for killing
                // someone
            }
            this.score -= 40;
            this.setHealth(0);
            this.dead = true;
        }
    }

    public void setTurnsLeft(int turns) {
        this.turns = turns;
    }

    public int getTurnsLeft() {
        return turns;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Player) {
            Player other = (Player) o;
            return this.name == other.name;
        }
        return false;
    }

    @Override
    public String toString() {
       String player = this.name 
        + ": HP: " + this.getHealth()
        + ", score: " + this.score 
        + ", RUB:" + this.getRubidium() 
        + ", EXP:" + this.getExplosium() 
        + ", SCR:" + this.getScrap()
        + ", prim. lvl:" + this.getPrimaryWeapon().getLevel() 
        + ", sec. lvl.:" + this.getSecondaryWeapon().getLevel();
        return player;
    }

    public Vector2d getPosition() {
        return position;
    }

    public void respawn() {
        this.position = this.spawn;
        this.setHealth(100);
    }

    public void useResources(TileType resource, int resources) throws ProtocolException {
        switch(resource) {
        case RUBIDIUM:
            if (resources <= this.getRubidium()) {
                this.rubidium = this.getRubidium() - resources;
            } 
            return;
        case EXPLOSIUM:
            if (resources <= this.getExplosium()) {
                this.explosium = this.getExplosium() - resources;
            }           
            return;
        case SCRAP:
            if (resources <= this.getScrap()) {
                this.scrap = this.getScrap() - resources;
            } 
           return;
        default:
        }
        throw new ProtocolException("Tried to upgrade the but not enough " + resource.toString());
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }

    public void send(Message message) {
        this.conn.sendMessage(message);
    }

    public ActionMessage next(long timeout, TimeUnit unit) {
        for(;;) {
            Message message = this.conn.next(timeout, unit);
            if (message == null) {
                return null;
            }
            if (message instanceof ActionMessage) {
                return (ActionMessage)message;
            }
        }
    }

    public boolean isDead() {
        return dead;
    }

    public void clear() {
       conn.clear();
    }

    public Weapon getPrimaryWeapon() {
        return primaryWeapon;
    }
    
    public Weapon getSecondaryWeapon() {
        return secondaryWeapon;
    }

    public int getRubidium() {
        return rubidium;
    }

    public void addRubidium() {
        this.rubidium++;
    }

    public int getExplosium() {
        return explosium;
    }

    public void addExplosium() {
        this.explosium++;
    }

    public int getScrap() {
        return scrap;
    }

    public void addScrap() {
        this.scrap++;
    }

    public int getHealth() {
        return health;
    }

    private void setHealth(int health) {
        this.health = health;
    }
}