package skyport.message.action;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.Player;
import skyport.game.TileType;
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

    @Override
    public void performAction(Player player, World map) throws ProtocolException {
        logger.debug(player + " upgrading his " + weapon + ".");
        
        Weapon weapon = Stream.of(player.getPrimaryWeapon(), player.getSecondaryWeapon())
                .filter(w -> w.getName().equals(this.weapon))
                .findFirst()
                .orElseThrow(() 
                        -> new ProtocolException("Tried to upgrade weapon '" + this.weapon + "', but doesn't have it."));
        logger.debug("Upgrading primary weapon (" + this.weapon + ").");
        
        TileType resource = weapon.resource();
        int resources = weapon.resources();
        player.useResources(resource, resources);
        weapon.upgrade();
    }
    
    @Override
    public String toString() {
        return from + " upgraded " + weapon + ".";
    }
}
