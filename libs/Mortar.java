import org.json.*;
import java.lang.Math;

public class Mortar {
    private Tile position;
    private Tile absoluteHitPosition;
    private Coordinate relativeTargetVector;
    private AIConnection dealingPlayer;
    private int level = 1;
    public Mortar(AIConnection dealingPlayerArg){
	dealingPlayer = dealingPlayerArg;
    }
    public void setPosition(Tile positionArg){
	position = positionArg;
    }
    public void setTarget(Coordinate relativeTargetVectorArg, int levelArg){
	relativeTargetVector = relativeTargetVectorArg;
	level = levelArg;
    }
    public boolean performShot(){
	Debug.game("'" + dealingPlayer.username + "' performing mortar shot at '"
			   + relativeTargetVector.getString() + "'");
	// TODO: check damage on all weapons
	int range = 3;
	int damage = 20;
	if(level == 2) {damage = 20; range = 4;}
	if(level == 3) {damage = 25; range = 5;}
	if(!isTileInRange(range)){
	    Debug.warn("Mortar shot out of range!");
	    return false;
	}
	setNewPositionBasedOnRelativeVector();
	explode(damage);
	return true;
    }
    private void setNewPositionBasedOnRelativeVector(){
	absoluteHitPosition = position;
	if(relativeTargetVector.j < 0){
	    for(int i = 0; i < -relativeTargetVector.j; i++){
		if(absoluteHitPosition.rightUp == null) {
		    // TODO fix this to not explode?
		    Debug.warn("Mortar reached end of map, exploding prematurely...");
		    break;
		}
		absoluteHitPosition = absoluteHitPosition.rightUp;
	    }
	}
	else {
	    for(int i = 0; i < relativeTargetVector.j; i++){
		if(absoluteHitPosition.leftDown == null) {
		    Debug.warn("Mortar reached end of map, exploding prematurely...");
		    break;
		}
		absoluteHitPosition = absoluteHitPosition.leftDown;
	    }
	}
	if(relativeTargetVector.k < 0){
	    for(int i = 0; i < -relativeTargetVector.k; i++){
		if(absoluteHitPosition.leftUp == null) {
		    Debug.warn("Mortar reached end of map, exploding prematurely...");
		    break;
		}
		absoluteHitPosition = absoluteHitPosition.leftUp;
	    }
	}
	else {
	    for(int i = 0; i < relativeTargetVector.k; i++){
		if(absoluteHitPosition.rightDown == null){
		    Debug.warn("Mortar reached end of map, exploding prematurely...");
		    break;
		}
		absoluteHitPosition = absoluteHitPosition.rightDown;
	    }
	}
    }
    private void explode(int damage){
	// TODO: implement bonuses for unused turns (also check other weapons)
	// TODO: rocks? void? spawn seems still vulnerable?
	if(absoluteHitPosition.tileType == TileType.ROCK
	   || absoluteHitPosition.tileType == TileType.VOID
	   || absoluteHitPosition.tileType == TileType.SPAWN){
	    Debug.warn("Mortar hit " + absoluteHitPosition.tileType + " tile, did not explode");
	    return;
	}
	int baseDamage = damage;
	int aoeDamage = 18;
	absoluteHitPosition.damageTile(baseDamage, dealingPlayer);
	if(absoluteHitPosition.up != null)
	    absoluteHitPosition.up.damageTile(aoeDamage, dealingPlayer);
	if(absoluteHitPosition.down != null)
	    absoluteHitPosition.down.damageTile(aoeDamage, dealingPlayer);
	if(absoluteHitPosition.rightDown != null)
	    absoluteHitPosition.rightDown.damageTile(aoeDamage, dealingPlayer);
	if(absoluteHitPosition.rightUp != null)
	    absoluteHitPosition.rightUp.damageTile(aoeDamage, dealingPlayer);
	if(absoluteHitPosition.leftDown != null)
	    absoluteHitPosition.leftDown.damageTile(aoeDamage, dealingPlayer);
	if(absoluteHitPosition.leftUp != null)
	    absoluteHitPosition.leftUp.damageTile(aoeDamage, dealingPlayer);
    }
    private boolean isTileInRange(int range){
	// simple approximative method derived from the cosine law, works
	// correctly for distances < ~8 tiles or so. Good enough for this game.
	int j = relativeTargetVector.j;
	int k = relativeTargetVector.k;
	assert(range < 8);
	int shotRange = (int)Math.ceil(Math.sqrt(Math.pow(j,2) + Math.pow(k,2) - 2*j*k*0.5));
	Debug.debug("shot range was: " + shotRange);
	return shotRange <= range;
    }
}
