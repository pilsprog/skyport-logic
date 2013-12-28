package skyport.game;

import java.util.Queue;

public class World {
    private Tile topTile;
    private Tile[][] tiles;

    private int jLength;
    private int kLength;

    private Queue<Tile> spawnpoints;

    public World(Tile[][] tiles, String filename, int dimension, Queue<Tile> spawnpoints) {
        this.jLength = dimension;
        this.kLength = dimension;
        this.tiles = tiles;
        this.topTile = tiles[0][0];
        this.spawnpoints = spawnpoints;

        Tile rightTile = topTile;
        while (rightTile.rightDown != null) {
            rightTile = rightTile.rightDown;
        }

        enumerateCoordinates(topTile);
        this.returnAsRowMajorMatrix();
    }

    public int getJLength() {
        return this.jLength;
    }

    public int getKLength() {
        return this.kLength;
    }

    public TileType[][] returnAsRowMajorMatrix() {
        Tile currentJTile = topTile;
        TileType matrix[][] = new TileType[jLength][kLength];
        for (int j = 0; j < jLength; j++) {
            Tile currentKTile = currentJTile;
            for (int k = 0; k < kLength; k++) {
                matrix[j][k] = currentKTile.tileType;
                currentKTile = currentKTile.rightDown;
                assert (matrix[j][k] == tiles[j][k].tileType);
            }
            currentJTile = currentJTile.leftDown;
        }

        return matrix;
    }

    public void enumerateCoordinates(Tile topTile) {
        Tile currentJTile = topTile;
        for (int j = 0; j < jLength; j++) {
            Tile currentKTile = currentJTile;
            for (int k = 0; k < kLength; k++) {
                currentKTile.coords = new Point(j, k);
                currentKTile = currentKTile.rightDown;
            }
            currentJTile = currentJTile.leftDown;
        }
    }

    public Queue<Tile> getSpawnpoints() {
        return spawnpoints;
    }

    public int verifyNumberOfPlayersOnBoard() {
        int players = 0;
        Tile currentJTile = topTile;
        for (int j = 0; j < jLength; j++) {
            Tile currentKTile = currentJTile;
            for (int k = 0; k < kLength; k++) {
                if (currentKTile.playerOnTile != null) {
                    players++;
                }
                currentKTile = currentKTile.rightDown;
            }
            currentJTile = currentJTile.leftDown;
        }
        return players;
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

    @Override
    public String toString() {
        String s = new String();
        Tile currentJTile = topTile;
        s += "[";
        for (int j = 0; j < jLength; j++) {
            Tile currentKTile = currentJTile;
            s += "[";
            for (int k = 0; k < kLength; k++) {
                s += currentKTile.tileType.name().substring(0, 1) + ", ";
                currentKTile = currentKTile.rightDown;
            }
            s += "],\n";
            currentJTile = currentJTile.leftDown;
        }
        s += "]";
        return s;
    }
}
