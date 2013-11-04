package skyport.game.weapon;

import java.util.List;

import skyport.debug.Debug;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.Tile;
import skyport.game.TileType;

public class Droid extends Weapon {
    private Tile position;
    private List<Direction> directions;
    private Player dealingPlayer;
    private int turnsLeft;

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
        Debug.game("'" + dealingPlayer.getName() + "' performing droid shot with " + directions.size() + " steps");
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
                Debug.debug("no more commands, detonating...");
                break;
            }
            if (!performOneStep(directions.get(i))) {
                Debug.warn("Droid hit inaccessible tile, detonating...");
                break;
            } else {
                validStepsTaken++;
                Debug.debug("droid executed command successfully, reading next instruction...");
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
        Debug.debug("droid moving '" + direction + "'");
        switch (direction) {
        case UP:
            if (position.up == null || position.up.tileType == TileType.SPAWN || position.up.tileType == TileType.ROCK || position.up.tileType == TileType.VOID) {
                return false;
            }
            position = position.up;
            return true;
        case DOWN:
            if (position.down == null || position.down.tileType == TileType.SPAWN || position.down.tileType == TileType.ROCK || position.down.tileType == TileType.VOID) {
                return false;
            }
            position = position.down;
            return true;
        case LEFT_UP:
            if (position.leftUp == null || position.leftUp.tileType == TileType.SPAWN || position.leftUp.tileType == TileType.ROCK || position.leftUp.tileType == TileType.VOID) {
                return false;
            }
            position = position.leftUp;
            return true;
        case LEFT_DOWN:
            if (position.leftDown == null || position.leftDown.tileType == TileType.SPAWN || position.leftDown.tileType == TileType.ROCK || position.leftDown.tileType == TileType.VOID) {
                return false;
            }
            position = position.leftDown;
            return true;
        case RIGHT_UP:
            if (position.rightUp == null || position.rightUp.tileType == TileType.SPAWN || position.rightUp.tileType == TileType.ROCK || position.rightUp.tileType == TileType.VOID) {
                return false;
            }
            position = position.rightUp;
            return true;
        case RIGHT_DOWN:
            if (position.rightDown == null || position.rightDown.tileType == TileType.SPAWN || position.rightDown.tileType == TileType.ROCK || position.rightDown.tileType == TileType.VOID) {
                return false;
            }
            position = position.rightDown;
            return true;
        }
        return true;
    }
}
