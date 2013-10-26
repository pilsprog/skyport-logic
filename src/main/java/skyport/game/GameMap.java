package skyport.game;

import java.util.ArrayList;
import java.util.List;

public class GameMap {
    @SuppressWarnings("unused")
    private int jLength;
    @SuppressWarnings("unused")
    private int kLength;
    @SuppressWarnings("unused")
    
    private List<List<TileType>> data;
    
    public GameMap(int jLength, int kLength, List<List<TileType>> data) {
        this.jLength = jLength;
        this.kLength = kLength;
        this.data = data;
    }

    public GameMap(int jLength, int kLength, String[][] data) {
        this.data = new ArrayList<>();
        for(String[] line : data) {
            List<TileType> l = new ArrayList<>();
            for(String tile : line) {
                switch(tile) {
                case "G":
                    l.add(TileType.GRASS);
                    break;
                case "V":
                    l.add(TileType.VOID);
                    break;
                case "S":
                    l.add(TileType.SPAWN);
                    break;
                case "E":
                    l.add(TileType.EXPLOSIUM);
                    break;
                case "R":
                    l.add(TileType.ROCK);
                    break;
                case "C":
                    l.add(TileType.SCRAP);
                    break;
                case "O":
                    l.add(TileType.ROCK);
                    break;
                }
            }
        }
    }
}
