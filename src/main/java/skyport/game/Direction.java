package skyport.game;

import com.google.gson.annotations.SerializedName;

public enum Direction {
    @SerializedName("up")         UP         (new Vector2d(-1,-1)),
    @SerializedName("down")       DOWN       (new Vector2d( 1, 1)),
    @SerializedName("right-up")   RIGHT_UP   (new Vector2d(-1, 0)),
    @SerializedName("right-down") RIGHT_DOWN (new Vector2d( 0, 1)),
    @SerializedName("left-up")    LEFT_UP    (new Vector2d( 0,-1)),
    @SerializedName("left-down")  LEFT_DOWN  (new Vector2d( 1, 0));
    
    public final Vector2d vec;
    
    private Direction(Vector2d p) {
        this.vec = p;
    }
   
}