package skyport.game.weapon;

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

    public void upgrade() {
        this.level++;
    }
    
    public abstract int damage();
    public abstract int aoe();

    @Override
    public boolean equals(Object o) {
        if (o instanceof Weapon) {
            Weapon other = (Weapon) o;
            return this.name.equals(other.name) && this.level == other.level;
        } else {
            return false;
        }
    }
}
