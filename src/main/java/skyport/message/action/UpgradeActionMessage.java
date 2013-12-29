package skyport.message.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.game.Player;

public class UpgradeActionMessage extends ActionMessage {
    private String weapon;

    private final Logger logger = LoggerFactory.getLogger(UpgradeActionMessage.class);

    public String getWeaponName() {
        return weapon;
    }

    private boolean subtractResourcesForWeaponUpgrade(Player player, String weapon, int currentLevel) {
        int resourcesToSubtract = 4;
        if (currentLevel == 2) {
            resourcesToSubtract = 5;
        }
        if (currentLevel == 3) {
            logger.warn(player + " tried to upgrade his " + weapon + ", but it is already level 3.");
            return false;
        }
        if (weapon.equals("laser")) {
            if (player.rubidiumResources >= resourcesToSubtract) {
                player.rubidiumResources -= resourcesToSubtract;
                return true;
            } else {
                logger.warn("Tried to upgrade the laser, but not enough rubidium");
                return false;
            }
        }
        if (weapon.equals("mortar")) {
            if (player.explosiumResources >= resourcesToSubtract) {
                player.explosiumResources -= resourcesToSubtract;
                return true;
            } else {
                logger.warn("Tried to upgrade the mortar, but not enough explosium");
                return false;
            }
        }
        if (weapon.equals("droid")) {
            if (player.scrapResources >= resourcesToSubtract) {
                player.scrapResources -= resourcesToSubtract;
                return true;
            } else {
                logger.warn("Tried to upgrade the droid, but not enough scrap");
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean performAction(Player player) {
        logger.debug(player + " upgrading his " + weapon);
        if (player.primaryWeapon.getName().equals(weapon)) {
            logger.debug("Upgrading primary weapon (" + weapon + ")");
            boolean success = subtractResourcesForWeaponUpgrade(player, weapon, player.primaryWeapon.getLevel());
            if (success) {
                player.primaryWeapon.upgrade();
                return true;
            } else {
                return false;
            }
        } else if (player.secondaryWeapon.getName().equals(weapon)) {
            boolean success = subtractResourcesForWeaponUpgrade(player, weapon, player.secondaryWeapon.getLevel());
            if (success) {
                player.secondaryWeapon.upgrade();
                return true;
            } else {
                return false;
            }
        } else {
            logger.warn(player + " tried to upgrade weapon '" + weapon + "', but doesn't have it.");
            return false;
        }
    }
    
    @Override
    public String toString() {
        return from + " upgraded " + weapon;
    }
}
