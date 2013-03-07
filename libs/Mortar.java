import org.json.*;
import java.lang.Math;

public class Mortar {
    private Tile position;
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
    public void performShot(){
	System.out.println("'" + dealingPlayer.username + "' performing mortar shot at '"
			   + relativeTargetVector.getString() + "'");
	int range = 3;
	int damage = 22;
	if(level == 2) {damage = 24; range = 4;}
	if(level == 3) {damage = 26; range = 5;}
	isTileInRange(range);
	setNewPositionBasedOnRelativeVector();
	explode(damage);
    }
    private void setNewPositionBasedOnRelativeVector(){
	// TODO: move to new position (or as close as possible, if on the border of the map)
    }
    private void explode(int damage){
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
    private boolean isTileInRange(int range){
	// simple approximative method derived from the cosine law, works
	// correctly for distances < ~8 tiles or so. Good enough for this game.
	int j = relativeTargetVector.j;
	int k = relativeTargetVector.k;
	assert(range < 8);
	return Math.ceil(Math.sqrt(Math.pow(j,2) + Math.pow(k,2) - 2*j*k*0.5)) <= range;
    }
}
