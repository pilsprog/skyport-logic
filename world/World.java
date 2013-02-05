public class World {
    Tile topTile;
    Tile bottomTile;
    Tile leftTile;
    Tile rightTile;
    public World(Tile topTileArg){
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
    }
}
