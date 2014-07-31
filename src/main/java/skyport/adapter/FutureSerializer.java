package skyport.adapter;

import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

@SuppressWarnings("rawtypes")
public class FutureSerializer implements JsonSerializer<Future> {

    @Override
    public JsonElement serialize(Future src, Type typeOfSrc, JsonSerializationContext context) {
        Object value;
        try {
            value = src.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return context.serialize(value);
    }


}
