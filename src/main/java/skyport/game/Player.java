package skyport.game;

import com.google.gson.annotations.Expose;


public class Player {
    public String name;
    public Tile position;
    public int health;
    public int score;
    
    public Weapon primaryWeapon;
    public Weapon secondaryWeapon;
    
    @Expose (serialize = false)
    public int rubidiumResources;
    @Expose (serialize = false)
    public int explosiumResources;
    @Expose (serialize = false)
    public int scrapResources;
    @Expose (serialize = false)
    public Tile spawnTile;

    public Player(
            String name,
            Tile position,
            int health,
            int score, 
            int rubidiumResources,
            int explosiumResources, 
            int scrapResources,
            Tile spawnTile) {
        this.position = position;
        this.health = health;
        
        this.score = score;
        this.rubidiumResources = rubidiumResources;
        this.explosiumResources = explosiumResources;
        this.scrapResources = scrapResources;
        this.spawnTile = spawnTile;
    }
    
    public void setLoadout(Weapon primary, Weapon secondary) {
        this.primaryWeapon = primary;
        this.secondaryWeapon = secondary;
    }
    
    public String getName() {
        return name;
    }
}