package skyport.game;

import skyport.game.weapon.Weapon;

import com.google.gson.annotations.Expose;


public class Player {
    public String name;
    public Tile position;
    public int health = 100;
    public int score = 0;
    
    public Weapon primaryWeapon;
    public Weapon secondaryWeapon;
    
    @Expose (serialize = false)
    public int rubidiumResources = 0;
    @Expose (serialize = false)
    public int explosiumResources = 0;
    @Expose (serialize = false)
    public int scrapResources = 0;
    
    @Expose (serialize = false)
    public Tile spawnTile;

    public Player(String name) {
        this.name = name;
    }
    
    public void setLoadout(Weapon primary, Weapon secondary) {
        this.primaryWeapon = primary;
        this.secondaryWeapon = secondary;
    }
    
    public String getName() {
        return name;
    }
}