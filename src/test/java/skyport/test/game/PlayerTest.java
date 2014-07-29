package skyport.test.game;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.ExecutionException;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import skyport.exception.ProtocolException;
import skyport.game.Player;
import skyport.game.World;
import skyport.network.Connection;
import skyport.test.Utils;

@RunWith(JUnit4.class)
public class PlayerTest {
    Player player;   
    World world;
    
    @Before
    public void initialize() throws ProtocolException, InterruptedException, ExecutionException {
       PropertyConfigurator.configure("log4j.properties");
       
       Connection conn = Utils.getMockConnection("player1");
       
       player = new Player(conn);  
       player.handshake();
       
    }
    
    @Test
    public void testSettingName() throws InterruptedException {
        assertEquals("player1", player.getName());
    }
}
