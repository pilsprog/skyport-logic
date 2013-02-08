import java.util.LinkedList;
import java.util.Collections;

public class World {
    Tile topTile;
    Tile bottomTile;
    Tile leftTile;
    Tile rightTile;
    int dimension;
    boolean debugWorld = false;
    LinkedList<Tile> freeSpawnpoints = new LinkedList<Tile>();
    public World(Tile topTileArg, String filename, int dimensionArg){
	dimension = dimensionArg;
	topTile = topTileArg;
	leftTile = topTile;
	while(leftTile.leftDown != null){
	    leftTile = leftTile.leftDown;
	}
	rightTile = topTile;
	while(rightTile.rightDown != null){
	    rightTile = rightTile.rightDown;
	}
	bottomTile = rightTile;
	while(bottomTile.leftDown != null){
	    bottomTile = bottomTile.leftDown;
	}
	System.out.println("Summary of world " + filename + ":");
	System.out.println("Total tiles: " + Tile.totalTiles);
	System.out.println("\tGrass tiles: " + Tile.grassTiles);
	System.out.println("\tRock tiles: " + Tile.rockTiles);
	System.out.println("\tVoid tiles: " + Tile.voidTiles);
	System.out.println("\tSpawn tiles: " + Tile.spawnTiles);
	System.out.println("Resources:");

	System.out.println("\tScrap tiles: " + Tile.scrapTiles + ", total scrap: " + Tile.scrapTiles*2);
	System.out.println("\tRubidium tiles: " + Tile.rubidiumTiles + ", total rubidium: "
			   + Tile.rubidiumTiles*2);
	System.out.println("\tExplosium tiles: " + Tile.explosiumTiles + ", total explosium: "
			   + Tile.explosiumTiles*2);
	enumerateCoordinates(topTile);
	findAndRandomizeSpawnpoints(topTile);
	performStructureConsistencyVerification(topTile);
	returnAsRowMajorMatrix();
    }
    public String[][] returnAsRowMajorMatrix(){
	Tile currentJTile = topTile;
	String matrix[][] = new String[dimension][dimension];
	for(int j = 0; j < dimension; j++){
	    Tile currentKTile = currentJTile;
	    for(int k = 0; k < dimension; k++){
		matrix[j][k] = currentKTile.id;
		currentKTile = currentKTile.rightDown;
	    }
	    currentJTile = currentJTile.leftDown;
	}
	if(debugWorld){
	    System.out.print("[");
	    for(int l = 0; l < dimension; l++){
		System.out.print("[");
		for(String s: matrix[l]){
		    System.out.print(s + ", ");
		}
		System.out.print("],\n");
	    }
	    System.out.println("]");
	}
	return matrix;
    }
    public void enumerateCoordinates(Tile topTile){
	Tile currentJTile = topTile;
	for(int j = 0; j < dimension; j++){
	    Tile currentKTile = currentJTile;
	    for(int k = 0; k < dimension; k++){
		currentKTile.coords = new Coordinate(j, k);
		currentKTile = currentKTile.rightDown;
	    }
	    currentJTile = currentJTile.leftDown;
	}
    }
    public void findAndRandomizeSpawnpoints(Tile topTile){
	Tile currentJTile = topTile;
	for(int j = 0; j < dimension; j++){
	    Tile currentKTile = currentJTile;
	    for(int k = 0; k < dimension; k++){
		if(currentKTile.tileType == TileType.SPAWN){
		    freeSpawnpoints.add(currentKTile);
		}
		currentKTile = currentKTile.rightDown;
	    }
	    currentJTile = currentJTile.leftDown;
	}
	System.out.println("Found " + freeSpawnpoints.size() + " spawnpoints. Randomizing...");
	Collections.shuffle(freeSpawnpoints);
    }
    public void performStructureConsistencyVerification(Tile topTile){
	int jWidthTop = 0;
	int kWidthTop = 0;
	int jWidthBot = 0;
	int kWidthBot = 0;
	int total = 0;
	System.out.println("DATASTRUCTURE CONSISTENCY VERIFICATION");
	Tile leftCornerTile = topTile;
	while(leftCornerTile.leftDown != null){
	    leftCornerTile = leftCornerTile.leftDown;
	    jWidthTop++;
	}
	System.out.println("\tlength of the J/UP edge: " + jWidthTop);
	Tile rightCornerTile = topTile;
	while(rightCornerTile.rightDown != null){
	    rightCornerTile = rightCornerTile.rightDown;
	    kWidthTop++;
	}
	System.out.println("\tlength of the K/UP edge: " + kWidthTop);
	Tile bottomTileLeft = leftCornerTile;
	while(bottomTileLeft.rightDown != null){
	    bottomTileLeft = bottomTileLeft.rightDown;
	    kWidthBot++;
	}
	System.out.println("\tlength of the K/DOWN edge: " + kWidthBot);
	
	Tile bottomTileRight = rightCornerTile;
	while(bottomTileRight.leftDown != null){
	    bottomTileRight = bottomTileRight.leftDown;
	    jWidthBot++;
	}
	System.out.println("\tlength of the J/DOWN edge: " + jWidthBot);

	if(bottomTileLeft == bottomTileRight){
	    System.out.println("\tBottom tile from the left equals the one from the right");
	}
	else {
	    System.out.println("\tBottom tile from the left does NOT equal the one from the right");
	}
	System.out.println("Verifying center up/down linkage");
	Tile topToBottomIterator = topTile;
	int topToBottomCounter = 0;
	while(topToBottomIterator.down != null){
	    topToBottomIterator = topToBottomIterator.down;
	    topToBottomCounter++;
	}
	if(topToBottomIterator == bottomTile){
	    System.out.println("downwards-linkage verified in " + topToBottomCounter + " steps.");
	}
	else{
	    System.out.println("downwards-linkage verification failed: failed to reach bottom after "
			       + topToBottomCounter + " steps.");
	}
	Tile bottomToTopIterator = bottomTile;
	int bottomToTopCounter = 0;
	while(bottomToTopIterator.up != null){
	    bottomToTopIterator = bottomToTopIterator.up;
	    bottomToTopCounter++;
	}
	if(bottomToTopIterator == topTile){
	    System.out.println("upwards-linkage verified in " + bottomToTopCounter + " steps.");
	}
	else {
	    System.out.println("upwards-linkage failed: failed to reach bottom after "
			       + bottomToTopCounter + " steps");
	}
	
    }

    public Tile getRandomSpawnpoint(){
	return freeSpawnpoints.poll();
    }
}
