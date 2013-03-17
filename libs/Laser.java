public class Laser {
    private Tile position;
    private String direction;
    private AIConnection dealingPlayer;
    private int turnsLeft;
    public Laser(AIConnection dealingPlayerArg, int turnsLeftArg){
	dealingPlayer = dealingPlayerArg;
	turnsLeft = turnsLeftArg;
    }
    public boolean setPosition(Tile positionArg){
	position = positionArg;
	return true;
    }
    public boolean setDirection(String directionArg){
	if(directionArg.equals("up") || directionArg.equals("down")
	   || directionArg.equals("left-up") || directionArg.equals("left-down")
	   || directionArg.equals("right-up") || directionArg.equals("right-down")){
	    direction = directionArg;
	    return true;
	}
	else {
	    return false;
	}
    }
    public Coordinate performShot(int level){
	Debug.game("'" + dealingPlayer.username + "' performing laser shot in direction "
			   + direction + "!");
	int range = 5;
	int baseDamage = 16;
	if(level == 2) {baseDamage = 18; range = 6;}
	if(level == 3) {baseDamage = 22; range = 7;}
	int damage = (int)Math.round(baseDamage + 0.2*turnsLeft*baseDamage);
	// damage adjusted for unused turns
	Tile currentTile = position;
	int i = 0;
	switch(direction){
	case "up":
	    for(i = 0; i < range; i++){
		if(currentTile.up == null) break;
		if(currentTile.up.tileType == TileType.ROCK) break;
		currentTile = currentTile.up;
		currentTile.damageTile(damage, dealingPlayer);
	    }
	    break;
	case "down":
	    for(i = 0; i < range; i++){
		if(currentTile.down == null) break;
		if(currentTile.down.tileType == TileType.ROCK) break;
		currentTile = currentTile.down;
		currentTile.damageTile(damage, dealingPlayer);

	    }
	    break;
	case "left-up":
	    for(i = 0; i < range; i++){
		if(currentTile.leftUp == null) break;
		if(currentTile.leftUp.tileType == TileType.ROCK) break;
		currentTile = currentTile.leftUp;
		currentTile.damageTile(damage, dealingPlayer);
	    }
	    break;
	case "left-down":
	    for(i = 0; i < range; i++){
		if(currentTile.leftDown == null) break;
		if(currentTile.leftDown.tileType == TileType.ROCK) break;
		currentTile = currentTile.leftDown;
		currentTile.damageTile(damage, dealingPlayer);
	    }
	    break;
	case "right-up":
	    for(i = 0; i < range; i++){
		if(currentTile.rightUp == null) break;
		if(currentTile.rightUp.tileType == TileType.ROCK) break;
		currentTile = currentTile.rightUp;
		currentTile.damageTile(damage, dealingPlayer);
	    }
	    break;
	case "right-down":
	    for(i = 0; i < range; i++){
		if(currentTile.rightDown == null) break;
		if(currentTile.rightDown.tileType == TileType.ROCK) break;
		currentTile = currentTile.rightDown;
		currentTile.damageTile(damage, dealingPlayer);
	    }
	    break;
	}
	return currentTile.coords;
    }
}
