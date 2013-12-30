package skyport.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.annotations.SerializedName;

public enum TileType {
    @SerializedName("G") GRASS (0),
    @SerializedName("O") ROCK (0),
    @SerializedName("S") SPAWN (0),
    @SerializedName("V") VOID (0),
    @SerializedName("R") RUBIDIUM (2),
    @SerializedName("E") EXPLOSIUM (2),
    @SerializedName("C") SCRAP (2);

    public final int resources;

    TileType(int resources) {
        this.resources = resources;
    }

    private static final Logger logger = LoggerFactory.getLogger(TileType.class);

    public static TileType tileType(String type) {
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
            logger.error("Error: Unknown tile type '" + type + "'.");
            return VOID;
        }
    }
}