package skyport.adapter;

import java.lang.reflect.Type;

import skyport.message.Message;
import skyport.message.action.ActionMessage;
import skyport.message.action.HandshakeMessage;
import skyport.message.action.LoadoutMessage;
import skyport.message.graphics.GraphicsHandshakeMessage;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class MessageDeserializer implements JsonDeserializer<Message> {

    @Override
    public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject obj = json.getAsJsonObject();
        String type = obj.get("message").getAsString().toLowerCase();
        
        Message message;
        switch (type) {
        case "connect":
            if(obj.get("password") != null) {
                message = context.deserialize(json, GraphicsHandshakeMessage.class);
            } else {
                message = context.deserialize(json, HandshakeMessage.class);
            }
            break;
        case "loadout":
            message = context.deserialize(json, LoadoutMessage.class);
            break;
        case "action":
            message = context.deserialize(json, ActionMessage.class);
            break;
        case "ready":
            message = new Message("ready");
            break;
        default:
            throw new JsonParseException("Message type was not recognized: " + json.toString());
        }
        
        return message;
    }

}
