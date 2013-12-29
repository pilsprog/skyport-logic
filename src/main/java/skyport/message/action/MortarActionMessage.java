package skyport.message.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.game.Player;
import skyport.game.Point;
import skyport.game.TileType;
import skyport.game.weapon.Mortar;

public class MortarActionMessage extends ActionMessage implements OffensiveAction {
    private Point coordinates;

    private transient final Logger logger = LoggerFactory.getLogger(MortarActionMessage.class);

    public Point getRelativeTarget() {
        return coordinates;
    }

    @Override
    public boolean performAction(Player player) {
        if (player.position.tileType == TileType.SPAWN) {
            logger.info("==> Player attempted to shoot mortar from spawn.");
            return false;
        }
        Mortar mortar;
        if (player.primaryWeapon.getName().equals("mortar")) {
            mortar = (Mortar) player.primaryWeapon;
        } else if (player.secondaryWeapon.getName().equals("mortar")) {
            mortar = (Mortar) player.secondaryWeapon;
        } else {
            logger.warn("User '" + player + "' attempted to shoot the mortar, but doesn't have it");
            return false;
        }
        mortar.setPosition(player.position);
        mortar.setTarget(coordinates);
        return mortar.performShot();
    
    @Override
    public String toString() {
        return from + " fired " + type + " at " + coordinates;
    }
}
