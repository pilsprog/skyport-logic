package skyport.adapter;

import java.io.IOException;

import skyport.game.Point;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class PointAdapter extends TypeAdapter<Point> {

    @Override
    public void write(JsonWriter writer, Point point) throws IOException {
        if (point == null) {
            writer.nullValue();
            return;
        }
        writer.value(point.getCompactString());
    }

    @Override
    public Point read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        return new Point(reader.nextString());
    }

}
