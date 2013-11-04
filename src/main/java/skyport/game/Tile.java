package skyport.game;

import skyport.debug.Debug;

public class Tile {
    static int grassTiles = 0;
    static int rockTiles = 0;
    static int voidTiles = 0;
    static int spawnTiles = 0;
    static int explosiumTiles = 0;
    static int rubidiumTiles = 0;
    static int scrapTiles = 0;
    static int totalTiles = 0;

    public Tile up = null;
    public Tile down = null;
    public Tile rightUp = null;
    public Tile rightDown = null;
    public Tile leftUp = null;
    public Tile leftDown = null;
    public int resources = 0;
    public TileType tileType;
    public String id = null;
    public Point coords;
    public Player playerOnTile = null;

    public Tile(String type) {
        id = type;
        if (type.equals("G")) {
            tileType = TileType.GRASS;
            grassTiles++;
        } else if (type.equals("V")) {
            tileType = TileType.VOID;
            voidTiles++;
        } else if (type.equals("S")) {
            tileType = TileType.SPAWN;
            spawnTiles++;
        } else if (type.equals("E")) {
            tileType = TileType.EXPLOSIUM;
            resources = 2;
            explosiumTiles++;
        } else if (type.equals("R")) {
            tileType = TileType.RUBIDIUM;
            resources = 2;
            rubidiumTiles++;
        } else if (type.equals("C")) {
            tileType = TileType.SCRAP;
            resources = 2;
            scrapTiles++;
        } else if (type.equals("O")) {
            tileType = TileType.ROCK;
            rockTiles++;
        } else {
            Debug.error("Error: Unknown tile type '" + type + "'");
        }
        totalTiles++;
    }
    
    public Tile getTileInDirection(Direction dir) {
        switch(dir) {
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
                Debug.game(tileType + " tile is depleted of resources and became a grass-tile.");
                tileType = TileType.GRASS;
                id = "G";
                // TODO: it may be of interest later to *Tiles-- and
                // grassTiles++.
                // Doesn't seem particularly important, though -- we currently
                // only use
                // it for the initial consistency check.
            }
            return true;
        }
        return false;
    }

    public void damageTile(int hitpoints, Player dealingPlayer) {
        Debug.highlight(coords.getString(), 255, 0, 0);
        if (playerOnTile == null) {
            return;
        } else {
            if (tileType == TileType.SPAWN) {
                Debug.info("Hit spawn tile, no damage received.");
            } else {
                playerOnTile.damagePlayer(hitpoints, dealingPlayer);
            }
        }
    }
}
