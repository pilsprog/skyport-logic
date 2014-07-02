package skyport.test.game;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.PropertyConfigurator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import skyport.game.Tile;
import skyport.game.TileType;
import skyport.game.Vector;
import skyport.game.World;
import skyport.game.WorldParser;

@RunWith(JUnit4.class)
public class WorldParserTest {
    World world;
    
    @Before
    public void initialize() throws IOException {
       PropertyConfigurator.configure("log4j.properties");
       WorldParser parser = new WorldParser();
       
       String string  = String.join("\n", 
               "players 1",
               "size 2",
               "description \"High Noon\"",
                 "    __",
                " __/S \\__",
                "/V \\__/G \\",
               "\\__/R \\__/",
                   "\\__/");
       
       File f = File.createTempFile("testmap", ".skyportmap");
       FileWriter writer = new FileWriter(f);
       writer.append(string);
       writer.close();
       
       this.world = parser.parse(f.getAbsolutePath());
       f.delete();
    }
    
    @Test
    public void parseWorldTileExistsTest() {
        assertTrue(world.tileAt(new Vector(0,0)).isPresent());
        assertTrue(world.tileAt(new Vector(0,1)).isPresent());
        assertTrue(world.tileAt(new Vector(1,0)).isPresent());
        assertTrue(world.tileAt(new Vector(1,1)).isPresent());
        assertFalse(world.tileAt(new Vector(2,2)).isPresent());
    }
    
    @Test
    public void parseWorldTileIsRightCoordTest() {
        Vector point = new Vector(0,0);
        Tile t = world.tileAt(point).get();
        assertEquals(t.coords, point);
        
        point = new Vector(0,1);
        t = world.tileAt(point).get();
        assertEquals(t.coords, point);
        
        point = new Vector(1,0);
        t = world.tileAt(point).get();
        assertEquals(t.coords, point);
        
        point = new Vector(1,1);
        t = world.tileAt(point).get();   
        assertEquals(t.coords, point);
    }
    
    @Test
    public void parseWorldTileIsCorrectType() {
        Vector point = new Vector(0,0);
        Tile t = world.tileAt(point).get();
        assertEquals(t.tileType, TileType.SPAWN);
        
        point = new Vector(0,1);
        t = world.tileAt(point).get();
        assertEquals(t.tileType, TileType.GRASS);
        
        point = new Vector(1,0);
        t = world.tileAt(point).get();
        assertEquals(t.tileType, TileType.VOID);
        
        point = new Vector(1,1);
        t = world.tileAt(point).get();   
        assertEquals(t.tileType, TileType.RUBIDIUM);
    }
}
