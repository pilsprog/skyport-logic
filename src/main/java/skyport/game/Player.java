package skyport.game;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.weapon.Weapon;
import skyport.message.ErrorMessage;
import skyport.message.HandshakeMessage;
import skyport.message.LoadoutMessage;
import skyport.message.Message;
import skyport.message.StatusMessage;
import skyport.message.action.ActionMessage;
import skyport.network.Connection;

public class Player {
    private Future<String> name;
    private int health = 100;
    private int score = 0;
    private Future<Weapon> primaryWeapon;
    private Future<Weapon> secondaryWeapon;
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
 
        CompletableFuture<HandshakeMessage> handshake = CompletableFuture.supplyAsync(() -> {
           for (;;) {
               Message message = this.conn.next();
               if (message instanceof HandshakeMessage) {
                   HandshakeMessage hs = (HandshakeMessage) message;
                   try {
                       hs.validate();
                       this.conn.sendMessage(new StatusMessage(true));
                       return hs;
                   } catch (ProtocolException e) {
                       this.conn.sendMessage(new ErrorMessage(e));
                   }
               }
           }
        });
        
        this.name = handshake.thenApply(HandshakeMessage::getName);
        
        CompletableFuture<LoadoutMessage> loadout = CompletableFuture.supplyAsync(() -> {
            try {
                handshake.get();
            } catch (Exception e1) {
                logger.info("Handshake failed. Aborting loadout..");
                return null;
            }
            for (;;) {
                Message message = this.conn.next();
                if (message instanceof LoadoutMessage) {
                    LoadoutMessage lo = (LoadoutMessage) message;
                    try {
                        lo.validate();
                        return lo;
                    } catch (ProtocolException e) {
                        this.conn.sendMessage(new ErrorMessage(e));
                    }
                }
            }
        });
        
        this.primaryWeapon = loadout.thenApply(LoadoutMessage::getPrimaryWeapon);
        this.secondaryWeapon = loadout.thenApply(LoadoutMessage::getSecondaryWeapon);
    }
    
    public String getName() {
        String name = "";
        try {
            name = this.name.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Getting name failed -> handshake didn't parse properly."); 
        }
        return name;
    }
    
    public void setSpawn(Vector2d spawn) {
        this.spawn = spawn;
        this.position = spawn;
    }
    
    public Vector2d getSpawn() {
        return spawn;
    }

    public void givePoints(int points) {
        logger.info("Got awarded " + points + " points.");
        this.score += points;
    }
    
    public int score() {
        return score;
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
        this.health = 100;
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
        Weapon weapon = null;
        try {
            weapon = this.primaryWeapon.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Loadout failed; couldn't get primary weapon.");
        }
        return weapon;
    }
    
    public Weapon getSecondaryWeapon() {
        Weapon weapon = null;
        try {
            weapon = this.secondaryWeapon.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Loadout failed; couldn't get primary weapon.");
        }
        return weapon;
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

    public void damage(int hp) {
        this.health -= hp;
        if (this.health <= 0) {
            this.dead = true;
            this.health = 0;
        }
        
    }
}