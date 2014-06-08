package skyport.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.InaccessibleTileException;
import skyport.exception.ProtocolException;
import skyport.game.weapon.Weapon;

public class Player {
    private String name;
    public Tile position;
    public int health = 100;
    public int score = 0;

    public Weapon primaryWeapon;
    public Weapon secondaryWeapon;

    public transient int rubidiumResources = 0;
    public transient int explosiumResources = 0;
    public transient int scrapResources = 0;

    public transient boolean dead = false;

    private transient Tile spawn;

    private transient int turns;

    private transient final Logger logger = LoggerFactory.getLogger(Player.class);

    public void setLoadout(Weapon primary, Weapon secondary) {
        this.primaryWeapon = primary;
        this.secondaryWeapon = secondary;
    }
    
    public synchronized void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
    
    public void setSpawn(Tile spawn) {
        this.spawn = spawn;
    }
    
    public Tile getSpawn() {
        return spawn;
    }

    public boolean move(Direction direction) throws ProtocolException {
        Tile next = this.position.getTileInDirection(direction);
        if (next != null && next.isAccessible()) {
            if (next.playerOnTile != null) {
                throw new ProtocolException("Player " + next.playerOnTile.getName() + " is already on this tile.");
            }
            this.position.playerOnTile = null;
            this.position = next;
            this.position.playerOnTile = this;
            return true;
        } else if (next == null) {
            throw new InaccessibleTileException(direction);
        } else {
            throw new InaccessibleTileException(next);
        }
    }

    void givePoints(int points) {
        logger.info("Got awarded " + points + " points.");
        this.score += points;
    }

    public void damagePlayer(int hitpoints, Player dealingPlayer) {
        if (this.health <= 0) {
            logger.warn("Player is already dead.");
            return;
        }
        logger.debug("'" + this.name + "' received " + hitpoints + " damage from '" + dealingPlayer.getName() + "'!");
        this.health -= hitpoints;
        if (!(dealingPlayer.name.equals(this.name))) {
            dealingPlayer.givePoints(hitpoints); // damaged user other than
            // self, award points
        }
        if (this.health <= 0) {
            logger.info("==> " + this.name + " got killed by " + dealingPlayer.name + ".");
            if (!(dealingPlayer.name.equals(this.name))) {
                dealingPlayer.givePoints(20); // 20 bonus points for killing
                // someone
            }
            this.score -= 40;
            this.health = 0;
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
        + ": HP: " + this.health
        + ", score: " + this.score 
        + ", RUB:" + this.rubidiumResources 
        + ", EXP:" + this.explosiumResources 
        + ", SCR:" + this.scrapResources
        + ", prim. lvl:" + this.primaryWeapon.getLevel() 
        + ", sec. lvl.:" + this.secondaryWeapon.getLevel();
        return player;
    }
}