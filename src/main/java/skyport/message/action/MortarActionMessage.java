package skyport.message.action;

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
    public boolean performAction(Player player) throws ProtocolException {
        if (player.getPosition().tileType == TileType.SPAWN) {
            throw new ProtocolException("Attempted to shoot mortar from spawn.");
        }
        Mortar mortar;
        if (player.primaryWeapon.getName().equals("mortar")) {
            mortar = (Mortar) player.primaryWeapon;
        } else if (player.secondaryWeapon.getName().equals("mortar")) {
            mortar = (Mortar) player.secondaryWeapon;
        } else {
            throw new ProtocolException("Attempted to shoot the mortar, but doesn't have it.");
        }
        mortar.setPosition(player.getPosition());
        mortar.setTarget(coordinates);
        return mortar.performShot(player, player.getTurnsLeft());
    }
    
    @Override
    public String toString() {
        return from + " fired " + type + " at " + coordinates + ".";
    }
}
