package skyport.game;


public class GameMap {
    @SuppressWarnings("unused")
    private int jLength;
    @SuppressWarnings("unused")
    private int kLength;
    
    @SuppressWarnings("unused")
    private String[][] data;
    
    public GameMap(int jLength, int kLength, String[][] data) {
        this.jLength = jLength;
        this.kLength = kLength;
        this.data = data;
    }
}
