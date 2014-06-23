package skyport.game.weapon;

import skyport.game.TileType;


public class Laser extends Weapon {
    
    public Laser() {
        super("laser");
    }
    
    public int range() {
        return 4 + level;
    }
    

    public int damage() {
        return 16 + 2 * (level - 1);
    }
    
    public int aoe() {
        return 0;
    }

    @Override
    public TileType resource() {
        return TileType.RUBIDIUM;
    }
}
