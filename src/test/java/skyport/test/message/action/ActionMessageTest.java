package skyport.test.message.action;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.Vector;
import skyport.game.Tile;
import skyport.game.TileType;
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
        tile1.rightDown = tile2;
        Tile tile3 = new Tile(TileType.GRASS);
        tile1.leftDown = tile3;
        Tile tile4 = new Tile(TileType.GRASS);
        tile2.leftDown = tile4;
        tile3.rightDown = tile4;
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
        message.setCoordinates(new Vector(1, 1));
        message.performAction(player1, world);
        
        assertEquals(100 - player1.primaryWeapon.damage(), player2.health);
        assertEquals(100 - player1.primaryWeapon.aoe(), player1.health);
    }
    
    @Test
    public void mineActionMessageTest() throws ProtocolException {
        player1.getPosition().tileType = TileType.RUBIDIUM;
        player1.getPosition().resources = 1;
        player1.setLoadout(new Droid(), new Laser());
        
        ActionMessage message = new MineActionMessage();
        message.performAction(player1, world);
        
        assertThat(player1.rubidiumResources, equalTo(1));
        assertThat(player1.explosiumResources, equalTo(0));
        assertThat(player1.scrapResources, equalTo(0));
        assertThat(player1.getPosition().tileType, equalTo(TileType.GRASS));
    }
    
    @Test
    public void moveActionMessageTest() throws ProtocolException {
        player1.setLoadout(new Droid(), new Laser());
        MoveActionMessage message = new MoveActionMessage();
        Tile start = player1.getPosition();
        Vector vector = player1.getPosition().coords;
        Tile end = world.tileAt(vector.pluss(Direction.LEFT_DOWN.vector)).get();
        assertNull(end.playerOnTile);
        
        message.setDirection(Direction.LEFT_DOWN);
        message.performAction(player1, world);
        
        assertNull(start.playerOnTile);
        assertEquals(player1, end.playerOnTile);
    }
    
    @Test(expected=ProtocolException.class)
    public void upgradeActionMessageWithoutWeaponTest() throws ProtocolException {
        player1.setLoadout(new Droid(), new Laser());
        UpgradeActionMessage message = new UpgradeActionMessage();
        message.setWeaponName("Mortar");
        
        message.performAction(player1, world);
    }
    
    @Test(expected=ProtocolException.class)
    public void upgradeActionMessageWithoutResourceTest() throws ProtocolException {
        player1.setLoadout(new Droid(), new Laser());
        UpgradeActionMessage message = new UpgradeActionMessage();
        message.setWeaponName("Droid");
        
        message.performAction(player1, world);
    }
    
    @Test
    public void upgradeActionMessageTest() throws ProtocolException {
        player1.setLoadout(new Droid(), new Laser());
        player1.scrapResources = 4;
        
        UpgradeActionMessage message = new UpgradeActionMessage();
        message.setWeaponName("droid");
        
        message.performAction(player1, world);
        
        int level = player1.primaryWeapon.getLevel();
        assertEquals(level, 2);
        int resources = player1.scrapResources;
        assertEquals(resources, 0);
    }
    
}
