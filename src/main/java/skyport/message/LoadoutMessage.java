package skyport.message;

import skyport.game.weapon.Weapon;


public class LoadoutMessage extends Message {
    private String primaryWeapon;
    private String secondaryWeapon;
    
    public Weapon getPrimaryWeapon() {
        return new Weapon(primaryWeapon);
    }
    
    public Weapon getSecondaryWeapon() {
        return new Weapon(secondaryWeapon);
    }
}
