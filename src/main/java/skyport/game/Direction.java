package skyport.game;

import com.google.gson.annotations.SerializedName;

public enum Direction {
    @SerializedName("up")         UP         (new Point(-1,-1)),
    @SerializedName("down")       DOWN       (new Point( 1, 1)),
    @SerializedName("right-up")   RIGHT_UP   (new Point(-1, 0)),
    @SerializedName("right-down") RIGHT_DOWN (new Point( 0, 1)),
    @SerializedName("left-up")    LEFT_UP    (new Point( 0,-1)),
    @SerializedName("left-down")  LEFT_DOWN  (new Point( 1, 0));
    
    public final Point point;
    
    private Direction(Point p) {
        this.point = p;
    }
   
}