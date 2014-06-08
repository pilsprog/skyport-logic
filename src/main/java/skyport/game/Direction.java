package skyport.game;

import com.google.gson.annotations.SerializedName;

public enum Direction {
    @SerializedName("up")         UP,
    @SerializedName("down")       DOWN,
    @SerializedName("rightUp")   RIGHT_UP,
    @SerializedName("rightDown") RIGHT_DOWN,
    @SerializedName("leftUp")    LEFT_UP,
    @SerializedName("leftDown")  LEFT_DOWN
}