package skyport.game;

public class Weapon {
    private String name;
    private int level;
    
    public Weapon(String name) {
        this.level = 1;
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void upgrade() {
        level++;
    }
}