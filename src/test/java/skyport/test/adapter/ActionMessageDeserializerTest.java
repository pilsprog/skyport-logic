package skyport.test.adapter;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import skyport.adapter.ActionMessageDeserializer;
import skyport.game.Direction;
import skyport.message.action.ActionMessage;
import skyport.message.action.DroidActionMessage;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;

@RunWith(JUnit4.class)
public class ActionMessageDeserializerTest {
    private Gson gson;
    
    @Before
    public void initialize() {
        gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
            .registerTypeAdapter(ActionMessage.class, new ActionMessageDeserializer())
            .create();
    }
    
    @Test
    public void deserializesDroidActionMessage() {
        String json = "{'message':'action', 'type':'droid', 'sequence':['up', 'rightUp', 'rightDown', 'down']}";
        ActionMessage message = gson.fromJson(json, ActionMessage.class);
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
