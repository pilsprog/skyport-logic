package skyport.test.message.action;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.Point;
import skyport.game.Tile;
import skyport.game.TileType;
import skyport.game.World;
import skyport.game.weapon.Droid;
import skyport.game.weapon.Laser;
import skyport.game.weapon.Mortar;
import skyport.message.action.DroidActionMessage;
import skyport.message.action.LaserActionMessage;
import skyport.message.action.MortarActionMessage;

@RunWith(JUnit4.class)
public class ActionMessageTest {
    Player player1;
    Player player2;
    
    World world;
    
    @Before
    public void initialize() {
        PropertyConfigurator.configure("log4j.properties");
        
        player1 = new Player();
        player1.setName("player1");
        
        player2 = new Player();
        player2.setName("player2");
        
        Tile tile1 = new Tile(TileType.GRASS);
        Tile tile2 = new Tile(TileType.GRASS);
        tile1.leftDown = tile2;
        Tile tile3 = new Tile(TileType.GRASS);
        tile1.rightDown = tile3;
        Tile tile4 = new Tile(TileType.GRASS);
        tile2.rightDown = tile4;
        tile3.leftDown = tile4;
        tile1.down = tile4;
        
        Tile[][] map = {{tile1, tile2},
                        {tile3, tile4}};
        
        map[0][0].playerOnTile = player1;
        player1.setPosition(map[0][0]);
        map[1][1].playerOnTile = player2;
        player2.setPosition(map[1][1]);
        world = new World(map, 2, null);
    }
    
    @Test
    public void droidActionMessageTest() throws ProtocolException {
        player1.primaryWeapon = new Droid();
        DroidActionMessage message = new DroidActionMessage();
        message.setPath(Arrays.asList(Direction.DOWN));
        message.performAction(player1, world);
        
        assertEquals(100 - player1.primaryWeapon.damage(), player2.health);
        assertEquals(100 - player1.primaryWeapon.aoe(), player1.health);
    }
    
    @Test
    public void laserActionMessageTest() throws ProtocolException {
        player1.primaryWeapon = new Laser();
        LaserActionMessage message = new LaserActionMessage();
        message.setDirection(Direction.DOWN);
        message.performAction(player1, world);
        
        assertEquals(100 - player1.primaryWeapon.damage(), player2.health);
        assertEquals(100 - player1.primaryWeapon.aoe(), player1.health);
    }
    
    @Test
    public void mortarActionMessageTest() throws ProtocolException {
        player1.primaryWeapon = new Mortar();
        MortarActionMessage message = new MortarActionMessage();
        message.setCoordinates(new Point(1, 1));
        message.performAction(player1, world);
        
        assertEquals(100 - player1.primaryWeapon.damage(), player2.health);
        assertEquals(100 - player1.primaryWeapon.aoe(), player1.health);
    }
}
