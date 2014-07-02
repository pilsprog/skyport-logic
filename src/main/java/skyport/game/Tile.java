package skyport.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;

public class Tile {

    public TileType tileType;
    public int resources;

    public Vector coords;
    public Player playerOnTile = null;

    private final Logger logger = LoggerFactory.getLogger(Tile.class);

    public Tile(TileType type) {
        tileType = type;
        this.resources = tileType.resources;
    }

    public boolean isAccessible() {
        // rock, void and spawn are not accessible.
        // grass, rubidium, explosium, scrap are accessible.
        return !(tileType == TileType.ROCK || tileType == TileType.SPAWN || tileType == TileType.VOID);
    }

    public boolean mineTile() throws ProtocolException {
        if (resources > 0) {
            resources--;
            if (resources == 0) {
                logger.info("==> " + tileType + " tile is depleted of resources and became a grass-tile.");
                tileType = TileType.GRASS;
            }
            return true;
        }
        throw new ProtocolException("Tile does not contain a resource.");
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
