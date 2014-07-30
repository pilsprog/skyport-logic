package skyport.game.weapon;

import skyport.exception.ProtocolException;
import skyport.game.TileType;

public abstract class Weapon {
    private String name;
    protected int level = 1;

    public Weapon(String name) {
        this.name = name;
    }

    public Weapon(String name, int level) {
        this.name = name;
        this.level = level;
    }

    public String getName() {
        return this.name;
    }

    public int getLevel() {
        return this.level;
    }

    public void upgrade() throws ProtocolException {
        if (this.level >= 3) {
            throw new ProtocolException("Tried to upgrade " + this.name + ", but it is already level 3.");
        }
        this.level++;
    }
    
    public abstract int damage();
    public abstract int aoe();
    public abstract TileType resource();
    
    public int resources() {
        return level == 1 ? 4 : 5;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Weapon) {
            Weapon other = (Weapon) o;
            return this.name.equals(other.name) && this.level == other.level;
        } 
        return false;
    }
}
