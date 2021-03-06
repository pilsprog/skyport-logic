package skyport.game;

import java.util.Optional;
import java.util.Queue;

public class World {  

    private Tile[][] data;
    private int jLength;
    private int kLength;
    
    private transient Queue<Vector2d> spawnpoints;

    public World(Tile[][] tiles, int dimension, Queue<Vector2d> spawnpoints) {
        this.jLength = dimension;
        this.kLength = dimension;
        this.data = tiles;
        this.spawnpoints = spawnpoints;
    }

    public int getJLength() {
        return this.jLength;
    }

    public int getKLength() {
        return this.kLength;
    }
    
    public Optional<Tile> tileAt(Vector2d p) {
        if (!(p.j >= 0 && p.j < this.jLength && p.k >= 0 && p.k < this.kLength)) {
            return Optional.empty();
        }
        return Optional.of(data[p.j][p.k]);
    }

    public Queue<Vector2d> getSpawnpoints() {
        return spawnpoints;
    }

    public int getNumberOfSpawnpoints() {
        return spawnpoints.size();
    }

    public void respawn(Player player) {
        Vector2d p = player.getPosition();
        data[p.j][p.k].playerOnTile = null;
        player.respawn();
        p = player.getPosition();
        data[p.j][p.k].playerOnTile = player; 
    }
}
