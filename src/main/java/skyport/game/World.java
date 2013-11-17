package skyport.game;

import java.util.Collections;
import java.util.LinkedList;

import skyport.debug.Debug;

public class World {
    private Tile topTile;
    private Tile[][] tiles;
    
    private int jLength;
    private int kLength;
    
    private LinkedList<Tile> freeSpawnpoints = new LinkedList<Tile>();

    public World(Tile[][] tiles, String filename, int dimension) {
        this.jLength = dimension;
        this.kLength = dimension;
        this.tiles = tiles;
        this.topTile = tiles[0][0];

        Tile rightTile = topTile;
        while (rightTile.rightDown != null) {
            rightTile = rightTile.rightDown;
        }
        if (Debug.developerMode) {
            System.out.println("Summary of world " + filename + ":");
            System.out.println("Total tiles: " + Tile.totalTiles);
            System.out.println("\tGrass tiles: " + Tile.grassTiles);
            System.out.println("\tRock tiles: " + Tile.rockTiles);
            System.out.println("\tVoid tiles: " + Tile.voidTiles);
            System.out.println("\tSpawn tiles: " + Tile.spawnTiles);
            System.out.println("Resources:");

            System.out.println("\tScrap tiles: " + Tile.scrapTiles + ", total scrap: " + Tile.scrapTiles * 2);
            System.out.println("\tRubidium tiles: " + Tile.rubidiumTiles + ", total rubidium: " + Tile.rubidiumTiles * 2);
            System.out.println("\tExplosium tiles: " + Tile.explosiumTiles + ", total explosium: " + Tile.explosiumTiles * 2);
        }
        enumerateCoordinates(topTile);
        findAndRandomizeSpawnpoints(topTile);
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
                assert(matrix[j][k] == tiles[j][k].tileType);
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

    public void findAndRandomizeSpawnpoints(Tile topTile) {
        Tile currentJTile = topTile;
        for (int j = 0; j < jLength; j++) {
            Tile currentKTile = currentJTile;
            for (int k = 0; k < kLength; k++) {
                if (currentKTile.tileType == TileType.SPAWN) {
                    freeSpawnpoints.add(currentKTile);
                }
                currentKTile = currentKTile.rightDown;
            }
            currentJTile = currentJTile.leftDown;
        }
        Debug.debug("Found " + freeSpawnpoints.size() + " spawnpoints. Randomizing...");
        Collections.shuffle(freeSpawnpoints);
    }

    public Tile getRandomSpawnpoint() {
        return freeSpawnpoints.poll();
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
    
    public String toString() {
        String s = new String();
        Tile currentJTile = topTile;
        s += "[";
        for (int j = 0; j < jLength; j++) {
            Tile currentKTile = currentJTile;
            for (int k = 0; k < kLength; k++) {
                s += currentKTile.tileType.name().substring(0, 1);
                currentKTile = currentKTile.rightDown;
            }
            s += "],\n";
            currentJTile = currentJTile.leftDown;
        }
        s+= "]";
        return s;
    }
}
