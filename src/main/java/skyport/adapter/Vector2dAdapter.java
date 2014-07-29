package skyport.adapter;

import java.io.IOException;

import skyport.game.Vector2d;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class Vector2dAdapter extends TypeAdapter<Vector2d> {

    @Override
    public void write(JsonWriter writer, Vector2d vector) throws IOException {
        if (vector == null) {
            writer.nullValue();
            return;
        }
        writer.value(vector.j + "," + vector.k);
    }

    @Override
    public Vector2d read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String[] tokens = reader.nextString().split(",");
        int j = Integer.parseInt(tokens[0]);
        int k = Integer.parseInt(tokens[1]);
        return new Vector2d(j, k);
    }

}
