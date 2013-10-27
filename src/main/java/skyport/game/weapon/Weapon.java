package skyport.game.weapon;

public class Weapon {
    private String name;
    private int level;
    
    public Weapon(String name) {
        this.name = name;
        this.level = 1;
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
}
