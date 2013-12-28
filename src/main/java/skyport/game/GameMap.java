package skyport.game;

public class GameMap {
    @SuppressWarnings("unused")
    private int jLength;
    @SuppressWarnings("unused")
    private int kLength;

    @SuppressWarnings("unused")
    private TileType[][] data;

    public GameMap(int jLength, int kLength, TileType[][] data) {
        this.jLength = jLength;
        this.kLength = kLength;
        this.data = data;
    }
}
