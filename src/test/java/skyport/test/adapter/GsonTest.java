package skyport.test.adapter;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import skyport.adapter.ActionMessageDeserializer;
import skyport.adapter.MessageDeserializer;
import skyport.adapter.TileSerializer;
import skyport.adapter.Vector2dAdapter;
import skyport.game.Direction;
import skyport.game.Tile;
import skyport.game.Vector2d;
import skyport.message.Message;
import skyport.message.action.ActionMessage;
import skyport.message.action.DroidActionMessage;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

@RunWith(JUnit4.class)
public class GsonTest {
    private Gson gson;
    
    @Before
    public void initialize() {
        PropertyConfigurator.configure("log4j.properties");
        
        gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
            .registerTypeAdapter(Vector2d.class, new Vector2dAdapter())
            .registerTypeAdapter(Message.class, new MessageDeserializer())
            .registerTypeAdapter(ActionMessage.class, new ActionMessageDeserializer())
            .registerTypeAdapter(Tile.class, new TileSerializer())
            .create();
    }
    
    @Test
    public void deserializesDroidActionMessage() {
        String json = "{'message':'action', 'type':'droid', 'sequence':['up', 'right-up', 'right-down', 'down']}";
        Message message = gson.fromJson(json, Message.class);
        assertTrue(message instanceof DroidActionMessage);
       
        DroidActionMessage action = (DroidActionMessage)message;
        assertEquals(action.getType(), "droid");   
        assertThat(action.getPath(), hasItems(Direction.UP, Direction.RIGHT_UP, Direction.RIGHT_DOWN, Direction.DOWN));
    }
    
    @Test(expected=JsonParseException.class)
    public void failsOnDeserializationOnNonsense() {
        String json = "{'message':'action', 'type':'nonsense'}";
        gson.fromJson(json, ActionMessage.class);
    }
   
}
