package skyport.test.game;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import skyport.exception.InaccessibleTileException;
import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.Tile;
import skyport.game.TileType;

@RunWith(JUnit4.class)
public class PlayerTest {
    Player player;
    
    @Before
    public void initialize() {
       player = new Player();
       player.position = new Tile(TileType.GRASS);
       player.position.up = new Tile(TileType.VOID);
    }
    
    @Test(expected=InaccessibleTileException.class)
    public void testMoveIntoNull() throws ProtocolException {
        player.move(Direction.DOWN);
    }
    
    @Test(expected=InaccessibleTileException.class)
    public void testMoveIntoVoid() throws ProtocolException {
        player.move(Direction.UP);
    }

}
