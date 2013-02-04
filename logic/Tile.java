public class Tile {
    public Tile(String type){
	System.out.println("Created tile of type '" + type + "'");
    }
    public Tile up = null;
    public Tile down = null;
    public Tile rightUp = null;
    public Tile rightDown = null;
    public Tile leftUp = null;
    public Tile leftDown = null;
    public boolean isAccessible = false;  // can tile be moved onto?
    public boolean isSpawn = false;       // spawn tiles reflect damage

    public int explosium = 0;
    public int rubidium = 0;
    public int scrap = 0;
}
