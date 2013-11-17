package skyport.message.action;

import java.util.List;

import skyport.debug.Debug;
import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.TileType;
import skyport.game.weapon.Droid;

public class DroidActionMessage extends ActionMessage {
    private List<Direction> sequence;
    
    public List<Direction> getPath() {
        return sequence;
    }

    public void setPath(List<Direction> sequence) {
       this.sequence = sequence;
    }

    @Override
    public boolean performAction(Player player) throws ProtocolException {
        if (player.position.tileType == TileType.SPAWN) {
            Debug.game("Player attempted to shoot droid from spawn.");
            return false;
        }
        Droid droid;
        if (player.primaryWeapon.getName().equals("droid")) {
            droid = (Droid)player.primaryWeapon;
        } else if (player.secondaryWeapon.getName().equals("droid")) {
            droid = (Droid)player.secondaryWeapon;
        } else {
            Debug.warn("User '" + player + "' attempted to shoot the droid, but doesn't have it");
            return false;
        }
        int range = 3;
        int droidLevel = droid.getLevel();
        if (droidLevel == 2) {
            range = 4;
        }
        if (droidLevel == 3) {
            range = 5;
        } // replicated here for more friendly error messages
        
        if (sequence.size() > range) {
            Debug.warn("Got " + sequence.size() + " commands for the droid, but your droids level (" + droidLevel + ") only supports " + range + " steps.");
            throw new ProtocolException("Got " + sequence.size() + " commands for the droid, but your droids level (" + droidLevel + ") only supports " + range + " steps.");
        }
        if (droid.setDirections(sequence, droidLevel)) {
            droid.setPosition(player.position);
            int stepsTaken = droid.performShot();

            this.sequence = sequence.subList(0, stepsTaken);
            Debug.debug("droid steps taken: " + stepsTaken);
            return true;
        } else {
            throw new ProtocolException("Invalid shot: unknown direction in droid sequence");
        }
    }
}
