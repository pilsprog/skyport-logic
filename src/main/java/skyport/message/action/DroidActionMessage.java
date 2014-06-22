package skyport.message.action;

import java.util.Arrays;
import java.util.List;

import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.TileType;
import skyport.game.weapon.Droid;

public class DroidActionMessage extends ActionMessage implements OffensiveAction {
    private List<Direction> sequence;

    public List<Direction> getPath() {
        return sequence;
    }

    public void setPath(List<Direction> sequence) {
        this.sequence = sequence;
    }

    @Override
    public void performAction(Player player) throws ProtocolException {
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

        droid.setDirections(this.sequence);
        droid.setPosition(player.getPosition());
        droid.performShot(player, player.getTurnsLeft());    
    }
    
    @Override
    public String toString() {
        String out = from + " fired " + type + " in sequence ";
        out += Arrays.toString(sequence.toArray());
        return out + ".";
    }
}
