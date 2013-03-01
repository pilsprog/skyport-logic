import org.json.*;

public class Droid {
    private Tile position;
    private String[] directions;
    private AIConnection dealingPlayer;
    private int level = 1;
    public Droid(AIConnection dealingPlayerArg){
	dealingPlayer = dealingPlayerArg;
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
    public void performShot(){
	System.out.println("'" + dealingPlayer.username + "' performing droid shot of length "
			   + directions.length);
	int range = 3;
	int damage = 22;
	if(level == 2) {damage = 24; range = 4;}
	if(level == 3) {damage = 26; range = 5;}
	Tile currentTile = position;
	// TODO: check droid can't go through any tiles it isn't supposed to go through
	for(int i = 0; i < range; i++){
	    if(i > directions.length - 1){
		System.out.println("no more commands, detonating...");
		break;
	    }
	    if(!performOneStep(directions[i])){
		System.out.println("inaccessible tile, detonating...");
		break;
	    }
	    else {
		System.out.println("droid executed command successfully, reading next instruction...");
	    }
	}
	explode(damage);
    }
    void explode(int damage){
	// TODO: implement bonuses for unused turns (also check other weapons)
	int baseDamage = damage;
	int aoeDamage = 10;
	position.damageTile(baseDamage, dealingPlayer);
	if(position.up != null) position.up.damageTile(aoeDamage, dealingPlayer);
	if(position.down != null) position.down.damageTile(aoeDamage, dealingPlayer);
	if(position.rightDown != null) position.rightDown.damageTile(aoeDamage, dealingPlayer);
	if(position.rightUp != null) position.rightUp.damageTile(aoeDamage, dealingPlayer);
	if(position.leftDown != null) position.leftDown.damageTile(aoeDamage, dealingPlayer);
	if(position.leftUp != null) position.leftUp.damageTile(aoeDamage, dealingPlayer);
    }
    boolean performOneStep(String direction){
	// TODO: unlike the laser, we need to return false here once we bump into someone.
	// also don't go over void tiles etc etc
	System.out.println("droid moving '" + direction + "'");
	switch(direction){
	case "up":
	    if(position.up == null || position.playerOnTile != null) return false;
	    position = position.up;
	    return true;
	case "down":
	    if(position.down == null || position.playerOnTile != null) return false;
	    position = position.down;
	    return true;
	case "left-up":
	    if(position.leftUp == null || position.playerOnTile != null) return false;
	    position = position.leftUp;
	    return true;
	case "left-down":
	    if(position.leftDown == null || position.playerOnTile != null) return false;
	    position = position.leftDown;
	    return true;
	case "right-up":
	    if(position.rightUp == null || position.playerOnTile != null) return false;
	    position = position.rightUp;
	    return true;
	case "right-down":
	    if(position.rightDown == null || position.playerOnTile != null) return false;
	    position = position.rightDown;
	    return true;
	}
	return true;
    }
}
