package skyport.game.weapon;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.Tile;
import skyport.game.TileType;

public class Droid extends Weapon {
    private Tile position;
    private List<Direction> directions;
    private Player dealingPlayer;
    private int turnsLeft;
    
    private final Logger logger = LoggerFactory.getLogger(Droid.class);

    public Droid(Player dealingPlayerArg, int turnsLeftArg) {
        super("droid");
        dealingPlayer = dealingPlayerArg;
        turnsLeft = turnsLeftArg;
    }

    public boolean setPosition(Tile positionArg) {
        position = positionArg;
        return true;
    }

    public boolean setDirections(List<Direction> directionSequence, int levelArg) {
        directions = directionSequence;
        level = levelArg;
        return true;
    }

    public int performShot() {
        logger.info("==> '" + dealingPlayer.getName() + "' performing droid shot with " + directions.size() + " steps");
        int range = 3;
        int damage = 22;
        int validStepsTaken = 0;
        if (level == 2) {
            damage = 24;
            range = 4;
        }
        if (level == 3) {
            damage = 26;
            range = 5;
        }

        for (int i = 0; i < range; i++) {
            if (i > directions.size() - 1) {
                logger.debug("no more commands, detonating...");
                break;
            }
            if (!performOneStep(directions.get(i))) {
                logger.warn("Droid hit inaccessible tile, detonating...");
                break;
            } else {
                validStepsTaken++;
                logger.debug("droid executed command successfully, reading next instruction...");
            }
        }
        explode(damage);
        return validStepsTaken;
    }

    void explode(int damage) {
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
        logger.debug("Droid moving '" + direction + "'");
        if (position.up == null || 
                position.up.tileType == TileType.SPAWN || 
                position.up.tileType == TileType.ROCK || 
                position.up.tileType == TileType.VOID) {
                return false;
        }
        switch (direction) {
        case UP:
            position = position.up;
            return true;
        case DOWN:
            position = position.down;
            return true;
        case LEFT_UP:
            position = position.leftUp;
            return true;
        case LEFT_DOWN:
            position = position.leftDown;
            return true;
        case RIGHT_UP:
            position = position.rightUp;
            return true;
        case RIGHT_DOWN:
            position = position.rightDown;
            return true;
        }
        return true;
    }
}
