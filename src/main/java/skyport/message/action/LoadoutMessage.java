package skyport.message.action;

import skyport.exception.ProtocolException;
import skyport.game.weapon.Droid;
import skyport.game.weapon.Laser;
import skyport.game.weapon.Mortar;
import skyport.game.weapon.Weapon;
import skyport.message.Message;

public class LoadoutMessage extends Message {
    public LoadoutMessage() {
        super("loadout");
    }

    private String primaryWeapon;
    private String secondaryWeapon;
    
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
    
    public void validate() throws ProtocolException {      
        if (primaryWeapon.equals(secondaryWeapon)) {
            throw new ProtocolException("Invalid loadout: Can't have the same weapon twice.");
        }
    }

    public Weapon getPrimaryWeapon() throws ProtocolException {
        return weapon(this.primaryWeapon);
    }
    
    public Weapon getSecondaryWeapon() throws ProtocolException {
        return weapon(this.secondaryWeapon);
    }
}
