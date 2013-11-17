package skyport.game;

import skyport.debug.Debug;

import com.google.gson.annotations.SerializedName;

public enum TileType {
    @SerializedName("G") GRASS, 
    @SerializedName("O") ROCK, 
    @SerializedName("S") SPAWN, 
    @SerializedName("V") VOID, 
    @SerializedName("R") RUBIDIUM, 
    @SerializedName("E") EXPLOSIUM,
    @SerializedName("C") SCRAP;
    
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
            Debug.error("Error: Unknown tile type '" + type + "'");
            return VOID;
        }
    }
}