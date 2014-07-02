package skyport.game;

import java.util.Optional;
import java.util.Queue;

public class World {  

    private Tile[][] data;
    private int jLength;
    private int kLength;
    
    private transient Tile topTile;
    private transient Queue<Tile> spawnpoints;

    public World(Tile[][] tiles, int dimension, Queue<Tile> spawnpoints) {
        this.jLength = dimension;
        this.kLength = dimension;
        this.data = tiles;
        this.topTile = tiles[0][0];
        this.spawnpoints = spawnpoints;

        Tile rightTile = topTile;
        while (rightTile.rightDown != null) {
            rightTile = rightTile.rightDown;
        }

        enumerateCoordinates(topTile);
    }

    public int getJLength() {
        return this.jLength;
    }

    public int getKLength() {
        return this.kLength;
    }
    
    public Optional<Tile> tileAt(Vector p) {
        if (!(p.j >= 0 && p.j < this.jLength && p.k >= 0 && p.k < this.kLength)) {
            return Optional.empty();
        }
        return Optional.of(data[p.j][p.k]);
    }

    private void enumerateCoordinates(Tile topTile) {
        Tile currentJTile = topTile;
        for (int j = 0; j < jLength; j++) {
            Tile currentKTile = currentJTile;
            for (int k = 0; k < kLength; k++) {
                currentKTile.coords = new Vector(j, k);
                currentKTile = currentKTile.rightDown;
            }
            currentJTile = currentJTile.leftDown;
        }
    }

    public Queue<Tile> getSpawnpoints() {
        return spawnpoints;
    }

    public int getNumberOfSpawnpoints() {
        int spawnpoints = 0;
        Tile currentJTile = topTile;
        for (int j = 0; j < jLength; j++) {
            Tile currentKTile = currentJTile;
            for (int k = 0; k < kLength; k++) {
                if (currentKTile.tileType == TileType.SPAWN) {
                    spawnpoints++;
                }
                currentKTile = currentKTile.rightDown;
            }
            currentJTile = currentJTile.leftDown;
        }
        return spawnpoints;
    }
}
