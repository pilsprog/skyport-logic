package skyport.message.action;

import skyport.debug.Debug;
import skyport.game.Player;
import skyport.game.Point;
import skyport.game.TileType;
import skyport.game.weapon.Mortar;

public class MortarActionMessage extends ActionMessage {
    private Point coordinates;
    
    public Point getRelativeTarget() {
        return coordinates;
    }

    @Override
    public boolean performAction(Player player) {
        if (player.position.tileType == TileType.SPAWN) {
            Debug.game("Player attempted to shoot mortar from spawn.");
            return false;
        }
        Mortar mortar;
        if (player.primaryWeapon.getName().equals("mortar")) {
            mortar = (Mortar) player.primaryWeapon;
        } else if (player.secondaryWeapon.getName().equals("mortar")) {
            mortar = (Mortar) player.secondaryWeapon;
        } else {
            Debug.warn("User '" + player.name + "' attempted to shoot the mortar, but doesn't have it");
            return false;
        }
        mortar.setPosition(player.position);
        mortar.setTarget(coordinates);
        return mortar.performShot();
    }
}
