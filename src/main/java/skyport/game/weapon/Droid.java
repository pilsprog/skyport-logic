package skyport.game.weapon;


public class Droid extends Weapon {

    public Droid() {
        super("droid");
    }
    
    public int range() {
        return 2 + level;
    }
    
    public int radius() {
        return 1;
    }
    
    public int damage() {
        return 20 + 2 * level;
    }
    
    public int aoe() {
        return 10;
    }
}
