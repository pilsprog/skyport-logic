package skyport.message.action;

import java.util.stream.Stream;

import skyport.exception.ProtocolException;
import skyport.game.Player;
import skyport.game.Point;
import skyport.game.TileType;
import skyport.game.weapon.Mortar;

public class MortarActionMessage extends ActionMessage implements OffensiveAction {
    private Point coordinates;
    
    public Point getRelativeTarget() {
        return coordinates;
    }

    @Override
    public void performAction(Player player) throws ProtocolException {
        if (player.getPosition().tileType == TileType.SPAWN) {
            throw new ProtocolException("Attempted to shoot mortar from spawn.");
        }
        
        Mortar mortar = Stream.of(player.primaryWeapon, player.secondaryWeapon)
                .filter(w -> w instanceof Mortar)
                .map(w -> (Mortar)w)
                .findFirst()
                .orElseThrow(() -> new ProtocolException("Attempted to shoot the mortar, but doesn't have it."));
        
        mortar.setPosition(player.getPosition());
        mortar.setTarget(coordinates);
        mortar.performShot(player, player.getTurnsLeft());
    }
    
    @Override
    public String toString() {
        return from + " fired " + type + " at " + coordinates + ".";
    }
}
