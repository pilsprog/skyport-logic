package skyport.message;

import skyport.game.weapon.Droid;
import skyport.game.weapon.Laser;
import skyport.game.weapon.Mortar;
import skyport.game.weapon.Weapon;

public class LoadoutMessage extends Message {
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
            return new Weapon(weapon);
        }
    }

    public Weapon getPrimaryWeapon() {
        return weapon(primaryWeapon);
    }

    public Weapon getSecondaryWeapon() {
        return weapon(secondaryWeapon);
    }
}
