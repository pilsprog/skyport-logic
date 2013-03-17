import org.json.*;

public class Droid {
    private Tile position;
    private String[] directions;
    private AIConnection dealingPlayer;
    private int level = 1;
    private int turnsLeft;
    public Droid(AIConnection dealingPlayerArg, int turnsLeftArg){
	dealingPlayer = dealingPlayerArg;
	turnsLeft = turnsLeftArg;
    }
    public boolean setPosition(Tile positionArg){
	position = positionArg;
	return true;
    }
    public boolean setDirections(JSONArray directionArray, int levelArg){
	// TODO: check you can't specify longer directions than
	// your droid level permits. The loop won't do too many
	// steps anyway, but we should send back a message.
	directions = new String[directionArray.length()];
	try {
	    for(int i = 0; i < directionArray.length(); i++){
		String direction = directionArray.getString(i);
		if(!(direction.equals("up") || direction.equals("down")
		     || direction.equals("left-up") || direction.equals("left-down")
		     || direction.equals("right-up") || direction.equals("right-down"))){
		    return false;
		}
		directions[i] = directionArray.getString(i);
	    }
	}
	catch(JSONException e){
	    return false;
	}
	level = levelArg;
	return true;
    }
    public int performShot(){
	Debug.game("'" + dealingPlayer.username + "' performing droid shot with "
			   + directions.length + " steps");
	int range = 3;
	int damage = 22;
	int validStepsTaken = 0;
	if(level == 2) {damage = 24; range = 4;}
	if(level == 3) {damage = 26; range = 5;}
	Tile currentTile = position;
	// TODO: check droid can't go through any tiles it isn't supposed to go through
	for(int i = 0; i < range; i++){
	    if(i > directions.length - 1){
		Debug.debug("no more commands, detonating...");
		break;
	    }
	    if(!performOneStep(directions[i])){
		Debug.warn("Droid hit inaccessible tile, detonating...");
		break;
	    }
	    else {
		validStepsTaken++;
		Debug.debug("droid executed command successfully, reading next instruction...");
	    }
	}
	explode(damage);
	return validStepsTaken;
    }
    void explode(int damage){
	int baseDamage = (int)Math.round(damage + 0.2*turnsLeft*damage);
	int aoeDamage = (int)Math.round(10 + 0.2*turnsLeft*10);
	position.damageTile(baseDamage, dealingPlayer);
	if(position.up != null) position.up.damageTile(aoeDamage, dealingPlayer);
	if(position.down != null) position.down.damageTile(aoeDamage, dealingPlayer);
	if(position.rightDown != null) position.rightDown.damageTile(aoeDamage, dealingPlayer);
	if(position.rightUp != null) position.rightUp.damageTile(aoeDamage, dealingPlayer);
	if(position.leftDown != null) position.leftDown.damageTile(aoeDamage, dealingPlayer);
	if(position.leftUp != null) position.leftUp.damageTile(aoeDamage, dealingPlayer);
    }
    boolean performOneStep(String direction){
	Debug.debug("droid moving '" + direction + "'");
	switch(direction){
	case "up":
	    if(position.up == null || position.up.tileType == TileType.SPAWN || position.up.tileType == TileType.ROCK
	       || position.up.tileType == TileType.VOID) return false;
	    position = position.up;
	    return true;
	case "down":
	    if(position.down == null || position.down.tileType == TileType.SPAWN || position.down.tileType == TileType.ROCK
	       || position.down.tileType == TileType.VOID) return false;
	    position = position.down;
	    return true;
	case "left-up":
	    if(position.leftUp == null || position.leftUp.tileType == TileType.SPAWN || position.leftUp.tileType == TileType.ROCK
	       || position.leftUp.tileType == TileType.VOID) return false;
	    position = position.leftUp;
	    return true;
	case "left-down":
	    if(position.leftDown == null || position.leftDown.tileType == TileType.SPAWN || position.leftDown.tileType == TileType.ROCK
	       || position.leftDown.tileType == TileType.VOID) return false;
	    position = position.leftDown;
	    return true;
	case "right-up":
	    if(position.rightUp == null || position.rightUp.tileType == TileType.SPAWN || position.rightUp.tileType == TileType.ROCK
	       || position.rightUp.tileType == TileType.VOID) return false;
	    position = position.rightUp;
	    return true;
	case "right-down":
	    if(position.rightDown == null || position.rightDown.tileType == TileType.SPAWN
	       || position.rightDown.tileType == TileType.ROCK || position.rightDown.tileType == TileType.VOID) return false;
	    position = position.rightDown;
	    return true;
	}
	return true;
    }
}
