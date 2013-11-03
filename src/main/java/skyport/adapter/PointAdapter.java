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
        writer.value(point.j + "," + point.k);
    }

    @Override
    public Point read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String[] tokens = reader.nextString().split(",");
        int j = Integer.parseInt(tokens[0]);
        int k = Integer.parseInt(tokens[1]);
        return new Point(j, k);
    }

}
