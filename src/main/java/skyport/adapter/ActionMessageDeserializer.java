package skyport.adapter;

import java.lang.reflect.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.message.action.ActionMessage;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ActionMessageDeserializer implements JsonDeserializer<ActionMessage> {
    private transient final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ActionMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();

        String type = obj.get("type").getAsString().toLowerCase();
        try {
            Class<?> c = Class.forName("skyport.message.action." 
                    + Character.toTitleCase(type.charAt(0)) + type.substring(1)
                    + "ActionMessage");
            return context.deserialize(json, c);
        } catch (ClassNotFoundException e) {
            logger.debug("Problem with action message:" + type + ".");
        }

        throw new JsonParseException("Unrecognized action type: " + type);
    }
}
