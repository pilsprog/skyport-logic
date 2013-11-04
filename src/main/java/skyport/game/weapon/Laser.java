package skyport.game.weapon;

import skyport.debug.Debug;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.Point;
import skyport.game.Tile;
import skyport.game.TileType;

public class Laser extends Weapon {
    private Tile position;
    private Direction direction;
    private Player dealingPlayer;
    private int turnsLeft;

    public Laser(Player dealingPlayerArg, int turnsLeftArg) {
        super("laser");
        dealingPlayer = dealingPlayerArg;
        turnsLeft = turnsLeftArg;
    }

    public void setPosition(Tile positionArg) {
        position = positionArg;
    }
    
    public void setTurnsLeft(int turns) {
        this.turnsLeft = turns;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Point performShot() {
        Debug.game("'" + dealingPlayer.getName() + "' performing laser shot in direction " + direction + "!");
        int range = 5;
        int baseDamage = 16;
        if (this.getLevel() == 2) {
            baseDamage = 18;
            range = 6;
        }
        if (this.getLevel() == 3) {
            baseDamage = 22;
            range = 7;
        }
        int damage = (int) Math.round(baseDamage + 0.2 * turnsLeft * baseDamage);
        // damage adjusted for unused turns
        Tile currentTile = position;
        int i = 0;
        switch (direction) {
        case UP:
            for (i = 0; i < range; i++) {
                if (currentTile.up == null) {
                    break;
                }
                if (currentTile.up.tileType == TileType.ROCK) {
                    break;
                }
                currentTile = currentTile.up;
                currentTile.damageTile(damage, dealingPlayer);
            }
            break;
        case DOWN:
            for (i = 0; i < range; i++) {
                if (currentTile.down == null) {
                    break;
                }
                if (currentTile.down.tileType == TileType.ROCK) {
                    break;
                }
                currentTile = currentTile.down;
                currentTile.damageTile(damage, dealingPlayer);

            }
            break;
        case LEFT_UP:
            for (i = 0; i < range; i++) {
                if (currentTile.leftUp == null) {
                    break;
                }
                if (currentTile.leftUp.tileType == TileType.ROCK) {
                    break;
                }
                currentTile = currentTile.leftUp;
                currentTile.damageTile(damage, dealingPlayer);
            }
            break;
        case LEFT_DOWN:
            for (i = 0; i < range; i++) {
                if (currentTile.leftDown == null) {
                    break;
                }
                if (currentTile.leftDown.tileType == TileType.ROCK) {
                    break;
                }
                currentTile = currentTile.leftDown;
                currentTile.damageTile(damage, dealingPlayer);
            }
            break;
        case RIGHT_UP:
            for (i = 0; i < range; i++) {
                if (currentTile.rightUp == null) {
                    break;
                }
                if (currentTile.rightUp.tileType == TileType.ROCK) {
                    break;
                }
                currentTile = currentTile.rightUp;
                currentTile.damageTile(damage, dealingPlayer);
            }
            break;
        case RIGHT_DOWN:
            for (i = 0; i < range; i++) {
                if (currentTile.rightDown == null) {
                    break;
                }
                if (currentTile.rightDown.tileType == TileType.ROCK) {
                    break;
                }
                currentTile = currentTile.rightDown;
                currentTile.damageTile(damage, dealingPlayer);
            }
            break;
        }
        return currentTile.coords;
    }
}
