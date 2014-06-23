package skyport.game.weapon;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.Tile;
import skyport.game.TileType;

public class Droid extends Weapon {
    private transient Tile position;
    private transient List<Direction> directions;

    private transient final Logger logger = LoggerFactory.getLogger(Droid.class);

    public Droid() {
        super("droid");
    }

    public boolean setPosition(Tile position) {
        this.position = position;
        return true;
    }
    
    public int range() {
        return 2 + level;
    }
    
    public int damage() {
        return 20 + 2 * level;
    }

    public void setDirections(List<Direction> sequence) throws ProtocolException {
        if (sequence.size() > this.range()) {
            logger.warn("Got " + sequence.size() + " commands for the droid, but your droids level (" + level + ") only supports " + range() + " steps.");
            throw new ProtocolException("Got " + sequence.size() + " commands for the droid, but your droids level (" + level + ") only supports " + range() + " steps.");
        }
        this.directions = sequence;
    }

    public void performShot(Player dealingPlayer, int turnsLeft) {
        logger.info("==> '" + dealingPlayer.getName() + "' performing droid shot with " + directions.size() + " steps.");
        int range = range();
        int damage = damage();
        int validSteps = 0;

        for (int i = 0; i < range; i++) {
            if (i > directions.size() - 1) {
                logger.debug("no more commands, detonating...");
                break;
            }
            if (!performOneStep(directions.get(i))) {
                logger.warn("Droid hit inaccessible tile, detonating...");
                break;
            } else {
                validSteps++;
                logger.debug("droid executed command successfully, reading next instruction...");
            }
        }
        explode(dealingPlayer, turnsLeft, damage);
        
        logger.debug("droid steps taken: " + validSteps);
    }

    private void explode(Player dealingPlayer, int turnsLeft, int damage) {
        int baseDamage = (int) Math.round(damage + 0.2 * turnsLeft * damage);
        int aoeDamage = (int) Math.round(10 + 0.2 * turnsLeft * 10);
        position.damageTile(baseDamage, dealingPlayer);
        if (position.up != null) {
            position.up.damageTile(aoeDamage, dealingPlayer);
        }
        if (position.down != null) {
            position.down.damageTile(aoeDamage, dealingPlayer);
        }
        if (position.rightDown != null) {
            position.rightDown.damageTile(aoeDamage, dealingPlayer);
        }
        if (position.rightUp != null) {
            position.rightUp.damageTile(aoeDamage, dealingPlayer);
        }
        if (position.leftDown != null) {
            position.leftDown.damageTile(aoeDamage, dealingPlayer);
        }
        if (position.leftUp != null) {
            position.leftUp.damageTile(aoeDamage, dealingPlayer);
        }
    }

    boolean performOneStep(Direction direction) {
        if (position == null 
                || position.tileType == TileType.SPAWN 
                || position.tileType == TileType.ROCK 
                || position.tileType == TileType.VOID) {
            return false;
        }
        logger.debug("Droid moving '" + direction + "'");
        switch (direction) {
        case UP:
            position = position.up;
            break;
        case DOWN:
            position = position.down;
            break;
        case LEFT_UP:
            position = position.leftUp;
            break;
        case LEFT_DOWN:
            position = position.leftDown;
            break;
        case RIGHT_UP:
            position = position.rightUp;
            break;
        case RIGHT_DOWN:
            position = position.rightDown;
            break;
        }
        return true;
            
    }
}
