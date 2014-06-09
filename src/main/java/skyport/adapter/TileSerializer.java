package skyport.adapter;

import java.lang.reflect.Type;

import skyport.game.Tile;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class TileSerializer implements JsonSerializer<Tile>{

    @Override
    public JsonElement serialize(Tile src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.tileType);
    }

}
