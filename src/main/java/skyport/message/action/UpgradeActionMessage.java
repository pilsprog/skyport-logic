package skyport.message.action;

import skyport.debug.Debug;
import skyport.game.Player;

public class UpgradeActionMessage extends ActionMessage {
    private String weapon;

    public String getWeaponName() {
        return weapon;
    }

    private boolean subtractResourcesForWeaponUpgrade(Player player, String weapon, int currentLevel) {
        int resourcesToSubtract = 4;
        if (currentLevel == 2) {
            resourcesToSubtract = 5;
        }
        if (currentLevel == 3) {
            Debug.warn(player + " tried to upgrade his " + weapon + ", but it is already level 3.");
            return false;
        }
        if (weapon.equals("laser")) {
            if (player.rubidiumResources >= resourcesToSubtract) {
                player.rubidiumResources -= resourcesToSubtract;
                return true;
            } else {
                Debug.warn("Tried to upgrade the laser, but not enough rubidium");
                return false;
            }
        }
        if (weapon.equals("mortar")) {
            if (player.explosiumResources >= resourcesToSubtract) {
                player.explosiumResources -= resourcesToSubtract;
                return true;
            } else {
                Debug.warn("Tried to upgrade the mortar, but not enough explosium");
                return false;
            }
        }
        if (weapon.equals("droid")) {
            if (player.scrapResources >= resourcesToSubtract) {
                player.scrapResources -= resourcesToSubtract;
                return true;
            } else {
                Debug.warn("Tried to upgrade the droid, but not enough scrap");
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean performAction(Player player) {

        Debug.debug(player + " upgrading his " + weapon);
        if (player.primaryWeapon.getName().equals(weapon)) {
            Debug.stub("upgrading primary weapon (" + weapon + ")");
            boolean success = subtractResourcesForWeaponUpgrade(player, weapon, player.primaryWeapon.getLevel());
            if (success) {
                player.primaryWeapon.upgrade();
                Debug.guiMessage(player + " upgrades his " + weapon);
                return true;
            } else {
                return false;
            }
        } else if (player.secondaryWeapon.getName().equals(weapon)) {
            boolean success = subtractResourcesForWeaponUpgrade(player, weapon, player.secondaryWeapon.getLevel());
            if (success) {
                player.secondaryWeapon.upgrade();
                Debug.guiMessage(player + " upgrades his " + weapon);
                return true;
            } else {
                return false;
            }
        } else {
            Debug.warn(player + " tried to upgrade weapon '" + weapon + "', but doesn't have it.");
            return false;
        }
    }
}
