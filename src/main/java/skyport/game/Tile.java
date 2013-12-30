package skyport.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tile {

    public Tile up = null;
    public Tile down = null;
    public Tile rightUp = null;
    public Tile rightDown = null;
    public Tile leftUp = null;
    public Tile leftDown = null;

    public TileType tileType;
    public int resources;

    public Point coords;
    public Player playerOnTile = null;

    private final Logger logger = LoggerFactory.getLogger(Tile.class);

    public Tile(String type) {
        tileType = TileType.tileType(type);
        this.resources = tileType.resources;
    }

    public Tile getTileInDirection(Direction dir) {
        switch (dir) {
        case UP:
            return this.up;
        case DOWN:
            return this.down;
        case RIGHT_UP:
            return this.rightUp;
        case RIGHT_DOWN:
            return this.rightDown;
        case LEFT_UP:
            return this.leftUp;
        case LEFT_DOWN:
            return this.leftDown;
        }
        return null;
    }

    public boolean isAccessible() {
        // rock, void and spawn are not accessible.
        // grass, rubidium, explosium, scrap are accessible.
        return !(tileType == TileType.ROCK || tileType == TileType.SPAWN || tileType == TileType.VOID);
    }

    public boolean mineTile() {
        if (resources > 0) {
            resources--;
            if (resources == 0) {
                logger.info("==> " + tileType + " tile is depleted of resources and became a grass-tile.");
                tileType = TileType.GRASS;
            }
            return true;
        }
        return false;
    }

    public void damageTile(int hitpoints, Player dealingPlayer) {
        if (playerOnTile == null) {
            return;
        } else {
            if (tileType == TileType.SPAWN) {
                logger.info("Hit spawn tile, no damage received.");
            } else {
                playerOnTile.damagePlayer(hitpoints, dealingPlayer);
            }
        }
    }
}
