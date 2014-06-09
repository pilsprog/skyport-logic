package skyport.message.action;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.TileType;
import skyport.game.weapon.Droid;

public class DroidActionMessage extends ActionMessage implements OffensiveAction {
    private List<Direction> sequence;

    private transient final Logger logger = LoggerFactory.getLogger(DroidActionMessage.class);

    public List<Direction> getPath() {
        return sequence;
    }

    public void setPath(List<Direction> sequence) {
        this.sequence = sequence;
    }

    @Override
    public boolean performAction(Player player) throws ProtocolException {
        if (player.getPosition().tileType == TileType.SPAWN) {
            throw new ProtocolException("Attempted to shoot droid from spawn.");
        }
        Droid droid;
        if (player.primaryWeapon.getName().equals("droid")) {
            droid = (Droid) player.primaryWeapon;
        } else if (player.secondaryWeapon.getName().equals("droid")) {
            droid = (Droid) player.secondaryWeapon;
        } else {
            throw new ProtocolException("Attempted to shoot the droid, but doesn't have it.");
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
            logger.warn("Got " + sequence.size() + " commands for the droid, but your droids level (" + droidLevel + ") only supports " + range + " steps.");
            throw new ProtocolException("Got " + sequence.size() + " commands for the droid, but your droids level (" + droidLevel + ") only supports " + range + " steps.");
        }
        if (droid.setDirections(sequence, droidLevel)) {
            droid.setPosition(player.getPosition());
            int stepsTaken = droid.performShot(player, player.getTurnsLeft());

            this.sequence = sequence.subList(0, stepsTaken);
            logger.debug("droid steps taken: " + stepsTaken);
            return true;
        } else {
            throw new ProtocolException("Invalid shot: unknown direction in droid sequence.");
        }
    }
    
    @Override
    public String toString() {
        String out = from + " fired " + type + " in sequence ";
        out += Arrays.toString(sequence.toArray());
        return out + ".";
    }
}
