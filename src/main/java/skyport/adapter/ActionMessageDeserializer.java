package skyport.adapter;

import java.lang.reflect.Type;

import skyport.message.action.ActionMessage;
import skyport.message.action.DroidActionMessage;
import skyport.message.action.LaserActionMessage;
import skyport.message.action.MineActionMessage;
import skyport.message.action.MortarActionMessage;
import skyport.message.action.MoveActionMessage;
import skyport.message.action.UpgradeActionMessage;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

public class ActionMessageDeserializer implements JsonDeserializer<ActionMessage> {

    @Override
    public ActionMessage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jobj = json.getAsJsonObject();
        
        String type = jobj.get("type").getAsString();
        switch(type) {
        case "droid":
            return context.deserialize(json, DroidActionMessage.class);
        case "laser":
            return context.deserialize(json, LaserActionMessage.class);
        case "mortar":
            return context.deserialize(json, MortarActionMessage.class);
        case "move":
            return context.deserialize(json, MoveActionMessage.class);
        case "mine":
            return context.deserialize(json, MineActionMessage.class);
        case "upgrade":
            return context.deserialize(json, UpgradeActionMessage.class);
        }
 
        throw new JsonParseException("Unrecognized action type: "+type);
    }
}
