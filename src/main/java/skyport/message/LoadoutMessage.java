package skyport.message;

import skyport.exception.ProtocolException;
import skyport.game.weapon.Droid;
import skyport.game.weapon.Laser;
import skyport.game.weapon.Mortar;
import skyport.game.weapon.Weapon;

public class LoadoutMessage extends Message {
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

    public Weapon getPrimaryWeapon() throws ProtocolException {
        return weapon(primaryWeapon);
    }

    public Weapon getSecondaryWeapon() throws ProtocolException {
        return weapon(secondaryWeapon);
    }
}
