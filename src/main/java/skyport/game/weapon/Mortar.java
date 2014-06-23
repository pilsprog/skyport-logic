package skyport.game.weapon;

public class Mortar extends Weapon {

    public Mortar() {
        super("mortar");
    }
    
    public int range() {
        return 1 + level;
    }

    public int damage() {
        return level == 3 ? 25 : 20;
    }
    
    public int aoe() {
        return 18;
    }
}
