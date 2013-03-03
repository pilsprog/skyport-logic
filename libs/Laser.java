public class Laser {
    private Tile position;
    private String direction;
    private AIConnection dealingPlayer;
    public Laser(AIConnection dealingPlayerArg){
	dealingPlayer = dealingPlayerArg;
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
	System.out.println("'" + dealingPlayer.username + "' performing laser shot in direction "
			   + direction + "!");
	int range = 5;
	int damage = 16;
	if(level == 2) {damage = 18; range = 6;}
	if(level == 3) {damage = 22; range = 7;}
	Tile currentTile = position;
	// TODO: rock tiles should not be transparent to lasers
	int i = 0;
	switch(direction){
	case "up":
	    for(i = 0; i < range; i++){
		if(currentTile.up == null) break;
		currentTile = currentTile.up;
		currentTile.damageTile(damage, dealingPlayer);
	    }
	    break;
	case "down":
	    for(i = 0; i < range; i++){
		if(currentTile.down == null) break;
		currentTile = currentTile.down;
		currentTile.damageTile(damage, dealingPlayer);

	    }
	    break;
	case "left-up":
	    for(i = 0; i < range; i++){
		if(currentTile.leftUp == null) break;
		currentTile = currentTile.leftUp;
		currentTile.damageTile(damage, dealingPlayer);
	    }
	    break;
	case "left-down":
	    for(i = 0; i < range; i++){
		if(currentTile.leftDown == null) break;
		currentTile = currentTile.leftDown;
		currentTile.damageTile(damage, dealingPlayer);
	    }
	    break;
	case "right-up":
	    for(i = 0; i < range; i++){
		if(currentTile.rightUp == null) break;
		currentTile = currentTile.rightUp;
		currentTile.damageTile(damage, dealingPlayer);
	    }
	    break;
	case "right-down":
	    for(i = 0; i < range; i++){
		if(currentTile.rightDown == null) break;
		currentTile = currentTile.rightDown;
		currentTile.damageTile(damage, dealingPlayer);
	    }
	    break;
	}
	return currentTile.coords;
    }
}
