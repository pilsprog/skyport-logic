package skyport.message;

import java.util.Arrays;
import java.util.List;

import skyport.exception.InvalidLoadoutException;
import skyport.exception.ProtocolException;
import skyport.game.weapon.Droid;
import skyport.game.weapon.Laser;
import skyport.game.weapon.Mortar;
import skyport.game.weapon.Weapon;

public class LoadoutMessage extends Message {
    public LoadoutMessage() {
        super("loadout");
    }

    private String primaryWeapon;
    private String secondaryWeapon;
    
    private Weapon weapon(String weapon) {
        switch(weapon.toLowerCase()) {
        case "droid":
            return new Droid();
        case "laser":
            return new Laser();
        case "mortar":
            return new Mortar();
        default:
            // This should never happen if the loadout is validated properly.
            return null;
        }
    }
    
    public void validate() throws ProtocolException {      
                
        List<String> weapons = Arrays.asList("droid", "laser", "mortar");
        if(!weapons.contains(primaryWeapon)) {
            throw new InvalidLoadoutException("No such weapon: '" + primaryWeapon +"'.");
        }
        
        if(!weapons.contains(secondaryWeapon)) {
            throw new InvalidLoadoutException("No such weapon: '" + secondaryWeapon +"'.");
            
        }
        
        if (primaryWeapon.equals(secondaryWeapon)) {
            throw new InvalidLoadoutException("Invalid loadout: Can't have the same weapon twice.");
        }
    }

    public Weapon getPrimaryWeapon()  {
        return weapon(this.primaryWeapon);
    }
    
    public Weapon getSecondaryWeapon()  {
        return weapon(this.secondaryWeapon);
    }
}
