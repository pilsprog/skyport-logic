package skyport.game;

import skyport.debug.Debug;
import skyport.exception.ProtocolException;
import skyport.game.weapon.Weapon;

import com.google.gson.annotations.Expose;


public class Player {
    public String name;
    public Tile position;
    public int health = 100;
    public int score = 0;
    
    @Expose (serialize = false)
    public boolean dead = false;
    
    public Weapon primaryWeapon;
    public Weapon secondaryWeapon;
    
    @Expose (serialize = false)
    public int rubidiumResources = 0;
    @Expose (serialize = false)
    public int explosiumResources = 0;
    @Expose (serialize = false)
    public int scrapResources = 0;
    
    @Expose (serialize = false)
    public Tile spawnTile;
    
    @Expose (serialize = false)
    private int turns;

    public Player(String name) {
        this.name = name;
    }
    
    public void setLoadout(Weapon primary, Weapon secondary) {
        this.primaryWeapon = primary;
        this.secondaryWeapon = secondary;
    }
    
    public String getName() {
        return name;
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
        } else {
            throw Util.throwInaccessibleTileException(direction.name(), next);
        }
    }
    
    void givePoints(int points) {
        Debug.info("got awarded " + points + " points");
        this.score += points;
    }

    
    public void damagePlayer(int hitpoints, Player dealingPlayer) {
        if (this.health <= 0) {
            Debug.warn("Player is already dead.");
            return;
        }
        Debug.stub("'" + this.name + "' received " + hitpoints + " damage from '" + dealingPlayer.getName() + "'!");
        this.health -= hitpoints;
        if (!(dealingPlayer.name.equals(this.name))) {
            dealingPlayer.givePoints(hitpoints); // damaged user other than
            // self, award points
        }
        if (this.health <= 0) {
            Debug.game(this.name + " got killed by " + dealingPlayer.name);
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
}