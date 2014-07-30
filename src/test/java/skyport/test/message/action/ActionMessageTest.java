package skyport.test.message.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.Tile;
import skyport.game.TileType;
import skyport.game.Vector2d;
import skyport.game.World;
import skyport.game.weapon.Droid;
import skyport.game.weapon.Laser;
import skyport.game.weapon.Mortar;
import skyport.message.action.ActionMessage;
import skyport.message.action.DroidActionMessage;
import skyport.message.action.LaserActionMessage;
import skyport.message.action.MineActionMessage;
import skyport.message.action.MortarActionMessage;
import skyport.message.action.MoveActionMessage;
import skyport.message.action.UpgradeActionMessage;
import skyport.network.Connection;
import skyport.test.Utils;

@RunWith(JUnit4.class)
public class ActionMessageTest {
    Player player1;
    Player player2;
    
    World world;
    
   
    
    @Before
    public void initialize() throws ProtocolException, InterruptedException, ExecutionException {
        PropertyConfigurator.configure("log4j.properties");
        
        Connection conn1 = Utils.getMockConnection("player1");
        player1 = spy(new Player(conn1));
        
        Connection conn2 = Utils.getMockConnection("player1");
        player2 = spy(new Player(conn2));
        
        Tile tile1 = new Tile(TileType.GRASS);
        Tile tile2 = new Tile(TileType.GRASS);
        Tile tile3 = new Tile(TileType.GRASS);
        Tile tile4 = new Tile(TileType.GRASS);

        tile1.coords = new Vector2d(0, 0);
        tile2.coords = new Vector2d(0, 1);
        tile3.coords = new Vector2d(1, 0);
        tile4.coords = new Vector2d(1, 1);
        
        Tile[][] map = {{tile1, tile2},
                        {tile3, tile4}};
        
        map[0][0].playerOnTile = player1;
        player1.setPosition(map[0][0].coords);
        map[1][1].playerOnTile = player2;
        player2.setPosition(map[1][1].coords);
        world = new World(map, 2, null);
    }
    
    @Test
    public void droidActionMessageTest() throws ProtocolException {
        doReturn(new Droid())
            .when(player1)
            .getPrimaryWeapon();
        
        DroidActionMessage message = new DroidActionMessage();
        message.setPath(Arrays.asList(Direction.DOWN));
        message.performAction(player1, world);
        
        assertEquals(100 - player1.getPrimaryWeapon().damage(), player2.getHealth());
        assertEquals(100 - player1.getPrimaryWeapon().aoe(), player1.getHealth());
    }
    
    @Test
    public void laserActionMessageTest() throws ProtocolException {
        doReturn(new Laser())
            .when(player1)
            .getPrimaryWeapon();
        
        LaserActionMessage message = new LaserActionMessage();
        message.setDirection(Direction.DOWN);
        message.performAction(player1, world);
        
        assertEquals(100 - player1.getPrimaryWeapon().damage(), player2.getHealth());
        assertEquals(100 - player1.getPrimaryWeapon().aoe(), player1.getHealth());
    }
    
    @Test
    public void mortarActionMessageTest() throws ProtocolException {
        doReturn(new Mortar())
            .when(player1)
            .getPrimaryWeapon();
        
        MortarActionMessage message = new MortarActionMessage();
        message.setCoordinates(new Vector2d(1, 1));
        message.performAction(player1, world);
        
        assertEquals(100 - player1.getPrimaryWeapon().damage(), player2.getHealth());
        assertEquals(100 - player1.getPrimaryWeapon().aoe(), player1.getHealth());
    }
    
    @Test
    public void mineActionMessageTest() throws ProtocolException {
        world.tileAt(player1.getPosition())
            .ifPresent(t -> {
                t.tileType = TileType.RUBIDIUM;
                t.resources = 1;
            });
        
        when(player1.getPrimaryWeapon())
            .thenReturn(new Laser());
        when(player1.getSecondaryWeapon())
            .thenReturn(new Droid());
        
        ActionMessage message = new MineActionMessage();
        message.performAction(player1, world);
        
        assertEquals(player1.getRubidium(), 1);
        assertEquals(player1.getExplosium(), 0);
        assertEquals(player1.getScrap(), 0);
        assertEquals(world.tileAt(player1.getPosition()).get().tileType,
                     TileType.GRASS);
    }
    
    @Test
    public void moveActionMessageTest() throws ProtocolException {
        MoveActionMessage message = new MoveActionMessage();
        Tile start = world.tileAt(player1.getPosition()).get();
        Vector2d vector = player1.getPosition();
        Tile end = world.tileAt(vector.plus(Direction.LEFT_DOWN.vec)).get();
        assertNull(end.playerOnTile);
        
        message.setDirection(Direction.LEFT_DOWN);
        message.performAction(player1, world);
        
        assertNull(start.playerOnTile);
        assertEquals(player1, end.playerOnTile);
    }
    
    @Test(expected=ProtocolException.class)
    public void upgradeActionMessageWithoutWeaponTest() throws ProtocolException {
        doReturn(new Droid())
            .when(player1)
            .getPrimaryWeapon();
        doReturn(new Laser())
            .when(player1)
            .getSecondaryWeapon();
        
        UpgradeActionMessage message = new UpgradeActionMessage();
        message.setWeaponName("Mortar");
        
        message.performAction(player1, world);
    }
    
    @Test(expected=ProtocolException.class)
    public void upgradeActionMessageWithoutResourceTest() throws ProtocolException {
        doReturn(new Laser())
            .when(player1)
            .getPrimaryWeapon();
        doReturn(new Droid())
            .when(player1)
            .getSecondaryWeapon();
        UpgradeActionMessage message = new UpgradeActionMessage();
        message.setWeaponName("Droid");
        
        message.performAction(player1, world);
    }
    
    @Test
    public void upgradeActionMessageTest() throws ProtocolException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        when(player1.getPrimaryWeapon())
            .thenReturn(new Laser());

        Field f = Player.class.getDeclaredField("secondaryWeapon");
        f.setAccessible(true);
        f.set(player1, new Droid());

        for(int i = 0; i < 4; i ++) {
            player1.addScrap();
        }
        
        UpgradeActionMessage message = new UpgradeActionMessage();
        message.setWeaponName("droid");
        
        message.performAction(player1, world);
        
        int level = player1.getSecondaryWeapon().getLevel();
        assertEquals(2, level);
        int resources = player1.getScrap();
        assertEquals(0, resources);
    }
    
}
