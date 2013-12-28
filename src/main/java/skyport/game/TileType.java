package skyport.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.annotations.SerializedName;

public enum TileType {
    @SerializedName("G") GRASS, 
    @SerializedName("O") ROCK, 
    @SerializedName("S") SPAWN, 
    @SerializedName("V") VOID, 
    @SerializedName("R") RUBIDIUM, 
    @SerializedName("E") EXPLOSIUM,
    @SerializedName("C") SCRAP;
    
    private static final Logger logger = LoggerFactory.getLogger(TileType.class);
    
    public static TileType getTile(String type) {
        switch (type) {
        case "G":
            return GRASS;
        case "V":
            return VOID;
        case "S":
            return SPAWN;
        case "E":
            return EXPLOSIUM;
        case "R":
            return RUBIDIUM;
        case "C":
            return SCRAP;
        case "O":
            return ROCK;
        default:
            logger.error("Error: Unknown tile type '" + type + "'");
            return VOID;
        }
    }
}