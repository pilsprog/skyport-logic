package skyport.test.game;

import org.apache.log4j.PropertyConfigurator;
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
import skyport.game.Vector;
import skyport.game.World;

@RunWith(JUnit4.class)
public class PlayerTest {
    Player player;
    
    World world;
    
    
    @Before
    public void initialize() {
       PropertyConfigurator.configure("log4j.properties");
       
       player = new Player();
       
       Tile tile1 = new Tile(TileType.GRASS);
       tile1.coords = new Vector(0, 0);
       Tile tile2 = new Tile(TileType.GRASS);
       tile2.coords = new Vector(0, 1);
       Tile tile3 = new Tile(TileType.GRASS);
       tile3.coords = new Vector(1,0);
       Tile tile4 = new Tile(TileType.VOID);
       tile4.coords = new Vector(1,1);
       
       Tile[][] map = {{tile1, tile2},
                       {tile3, tile4}};
       
       map[0][0].playerOnTile = player;
       player.setPosition(map[0][0]);
       world = new World(map, 2, null);
    }
    
    @Test(expected=InaccessibleTileException.class)
    public void testMoveIntoNull() throws ProtocolException {
        player.move(Direction.UP, world);
    }
    
    @Test(expected=InaccessibleTileException.class)
    public void testMoveIntoVoid() throws ProtocolException {
        player.move(Direction.DOWN, world);
    }

}
