package skyport.message.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.Player;
import skyport.game.World;
import skyport.game.weapon.Droid;
import skyport.game.weapon.Laser;
import skyport.game.weapon.Mortar;
import skyport.game.weapon.Weapon;

public class LoadoutMessage extends ActionMessage {
    private String primaryWeapon;
    private String secondaryWeapon;
    
    private transient final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    private Weapon weapon(String weapon) throws ProtocolException {
        switch(weapon.toLowerCase()) {
        case "droid":
            return new Droid();
        case "laser":
            return new Laser();
        case "mortar":
            return new Mortar();
        default:
            throw new ProtocolException("No such weapon: " + weapon);
        }
    }
    
    public void performAction(Player player, World map) throws ProtocolException {
        
        Weapon primary = weapon(primaryWeapon);
        Weapon secondary = weapon(secondaryWeapon);
        
        if (primary.equals(secondary)) {
            throw new ProtocolException("Invalid loadout: Can't have the same weapon twice.");
        }

        player.setLoadout(primary, secondary);

        logger.info(player.getName() + " selected loadout: " + player.primaryWeapon.getName() + " and " + player.secondaryWeapon.getName() + ".");

    }
}
