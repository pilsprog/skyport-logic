package skyport.game.weapon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.Point;
import skyport.game.Tile;
import skyport.game.TileType;

public class Laser extends Weapon {
    private transient Tile position;
    private transient Direction direction;

    private transient final Logger logger = LoggerFactory.getLogger(Laser.class);

    public Laser() {
        super("laser");
    }
    
    public int range() {
        return 4 + level;
    }

    public int damage() {
        return 16 + 2 * (level - 1);
    }

    public void setPosition(Tile position) {
        this.position = position;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Point performShot(Player dealingPlayer, int turnsLeft) {
        logger.info("==> '" + dealingPlayer.getName() + "' performing laser shot in direction " + direction + "!");
        int range = range();
        int baseDamage = damage();
       
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
