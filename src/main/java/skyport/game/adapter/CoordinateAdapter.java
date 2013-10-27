package skyport.game.adapter;

import java.io.IOException;

import skyport.game.Coordinate;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class CoordinateAdapter extends TypeAdapter<Coordinate> {

    @Override
    public void write(JsonWriter writer, Coordinate point) throws IOException {
        if (point == null) {
            writer.nullValue();
            return;
        }
        writer.value(point.getCompactString());
    }

    @Override
    public Coordinate read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        return new Coordinate(reader.nextString());
    }

}
