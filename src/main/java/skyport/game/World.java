package skyport.game;

import java.util.Collections;
import java.util.LinkedList;

import skyport.debug.Debug;

public class World {
    private Tile topTile;
    private Tile bottomTile;
    
    private int jLength;
    private int kLength;
    
    private LinkedList<Tile> freeSpawnpoints = new LinkedList<Tile>();

    public World(Tile topTileArg, String filename, int dimension) {
        this.jLength = dimension;
        this.kLength = dimension;
        topTile = topTileArg;

        Tile rightTile = topTile;
        while (rightTile.rightDown != null) {
            rightTile = rightTile.rightDown;
        }
        bottomTile = rightTile;
        while (bottomTile.leftDown != null) {
            bottomTile = bottomTile.leftDown;
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
        if (Debug.developerMode) {
            performStructureConsistencyVerification(topTile);
        }
        returnAsRowMajorMatrix();
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

    public void performStructureConsistencyVerification(Tile topTile) {
        int jWidthTop = 0;
        int kWidthTop = 0;
        int jWidthBot = 0;
        int kWidthBot = 0;

        System.out.println("DATASTRUCTURE CONSISTENCY VERIFICATION");
        Tile leftCornerTile = topTile;
        while (leftCornerTile.leftDown != null) {
            leftCornerTile = leftCornerTile.leftDown;
            jWidthTop++;
        }
        System.out.println("\tlength of the J/UP edge: " + jWidthTop);
        Tile rightCornerTile = topTile;
        while (rightCornerTile.rightDown != null) {
            rightCornerTile = rightCornerTile.rightDown;
            kWidthTop++;
        }
        System.out.println("\tlength of the K/UP edge: " + kWidthTop);
        Tile bottomTileLeft = leftCornerTile;
        while (bottomTileLeft.rightDown != null) {
            bottomTileLeft = bottomTileLeft.rightDown;
            kWidthBot++;
        }
        System.out.println("\tlength of the K/DOWN edge: " + kWidthBot);

        Tile bottomTileRight = rightCornerTile;
        while (bottomTileRight.leftDown != null) {
            bottomTileRight = bottomTileRight.leftDown;
            jWidthBot++;
        }
        System.out.println("\tlength of the J/DOWN edge: " + jWidthBot);

        if (bottomTileLeft == bottomTileRight) {
            System.out.println("\tBottom tile from the left equals the one from the right");
        } else {
            System.out.println("\tBottom tile from the left does NOT equal the one from the right");
        }
        System.out.println("Verifying center up/down linkage");
        Tile topToBottomIterator = topTile;
        int topToBottomCounter = 0;
        while (topToBottomIterator.down != null) {
            topToBottomIterator = topToBottomIterator.down;
            topToBottomCounter++;
        }
        if (topToBottomIterator == bottomTile) {
            System.out.println("downwards-linkage verified in " + topToBottomCounter + " steps.");
        } else {
            System.out.println("downwards-linkage verification failed: failed to reach bottom after " + topToBottomCounter + " steps.");
        }
        Tile bottomToTopIterator = bottomTile;
        int bottomToTopCounter = 0;
        while (bottomToTopIterator.up != null) {
            bottomToTopIterator = bottomToTopIterator.up;
            bottomToTopCounter++;
        }
        if (bottomToTopIterator == topTile) {
            System.out.println("upwards-linkage verified in " + bottomToTopCounter + " steps.");
        } else {
            System.out.println("upwards-linkage failed: failed to reach bottom after " + bottomToTopCounter + " steps");
        }

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
