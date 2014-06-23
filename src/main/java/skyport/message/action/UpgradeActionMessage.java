package skyport.message.action;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.Player;
import skyport.game.World;
import skyport.game.weapon.Weapon;

public class UpgradeActionMessage extends ActionMessage {
    private String weapon;

    private transient final Logger logger = LoggerFactory.getLogger(UpgradeActionMessage.class);

    public String getWeaponName() {
        return weapon;
    }
    
    public void setWeaponName(String name) {
        this.weapon = name;
    }

    private boolean subtractResourcesForWeaponUpgrade(Player player, String weapon, int currentLevel) throws ProtocolException {
        int resourcesToSubtract = 4;
        if (currentLevel == 2) {
            resourcesToSubtract = 5;
        }
        if (currentLevel == 3) {
            throw new ProtocolException("Tried to upgrade " + weapon + ", but it is already level 3.");
        }
        if (weapon.equals("laser")) {
            if (player.rubidiumResources >= resourcesToSubtract) {
                player.rubidiumResources -= resourcesToSubtract;
                return true;
            } else {
                throw new ProtocolException("Tried to upgrade the laser, but not enough rubidium.");
            }
        }
        if (weapon.equals("mortar")) {
            if (player.explosiumResources >= resourcesToSubtract) {
                player.explosiumResources -= resourcesToSubtract;
                return true;
            } else {
                throw new ProtocolException("Tried to upgrade the mortar, but not enough explosium.");
            }
        }
        if (weapon.equals("droid")) {
            if (player.scrapResources >= resourcesToSubtract) {
                player.scrapResources -= resourcesToSubtract;
                return true;
            } else {
                throw new ProtocolException("Tried to upgrade the droid, but not enough scrap.");
            }
        }
        return false;
    }

    @Override
    public void performAction(Player player, World map) throws ProtocolException {
        logger.debug(player + " upgrading his " + weapon + ".");
        
        Weapon weapon = Stream.of(player.primaryWeapon, player.secondaryWeapon)
                .filter(w -> w.getName().equals(this.weapon))
                .findFirst()
                .orElseThrow(() 
                        -> new ProtocolException("Tried to upgrade weapon '" + this.weapon + "', but doesn't have it."));
        logger.debug("Upgrading primary weapon (" + this.weapon + ").");
  
        subtractResourcesForWeaponUpgrade(player, this.weapon, player.primaryWeapon.getLevel());
        player.primaryWeapon.upgrade();
        weapon.upgrade();
    }
    
    @Override
    public String toString() {
        return from + " upgraded " + weapon + ".";
    }
}
