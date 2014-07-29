package skyport.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldParser {
    private int jLength;
    private int kLength;
    private String description;
    private int players;
    private int ignoredLines = 0;
    private Scanner scanner;

    private final Logger logger = LoggerFactory.getLogger(WorldParser.class);

    public World parse(String file) throws FileNotFoundException {
        logger.info("Parsing map '" + file + "'");
        this.scanner = new Scanner(new File(file));
        parseHeader();
        logger.info("Players: " + players);
        logger.info("size: " + jLength);
        logger.info("description: '" + description + "'");
        Tile[][] tiles = parseBody();
        logger.debug("Done parsing. Ignored " + ignoredLines + " empty lines.");

        Queue<Vector2d> spawnpoints = new LinkedList<>();
        for (Tile[] ts : tiles) {
            for (Tile t : ts) {
                if (t.tileType.equals(TileType.SPAWN)) {
                    spawnpoints.add(t.coords);
                }
            }
        }

        return new World(tiles, jLength, spawnpoints);
    }

    private void parseHeader() {
        String playersArray[] = scanner.nextLine().split("\\s");
        String sizeArray[] = scanner.nextLine().split("\\s");
        String desc[] = scanner.nextLine().split("\\s");

        int numPlayers = Integer.parseInt(playersArray[1]);
        assert (numPlayers <= 8);
        assert (numPlayers > 1);
        int dimensionsInteger = Integer.parseInt(sizeArray[1]);
        assert (dimensionsInteger >= 2);
        assert (dimensionsInteger < 100);

        players = numPlayers;
        description = String.join(" ", Arrays.copyOfRange(desc, 1, desc.length));
        jLength = dimensionsInteger;
        kLength = dimensionsInteger;
    }

    private Tile[][] parseBody() {
        int currentLength = 1;
        int a = 0;

        Tile[][] tiles = new Tile[jLength][];
        for (int j = 0; j < tiles.length; j++) {
            tiles[j] = new Tile[kLength];
        }

        // increasing part of the algorithm
        while (a < jLength) {
            String lines[] = nextLine();
            if (lines.length == 0) {
                continue;
            }
            if (lines.length != currentLength) {
                logger.warn("Error: expected this line to have length " + currentLength + ", but got " + lines.length);
                continue;
            }          

            tiles[a][0] = new Tile(TileType.tileType(lines[0]));

            for (int b = 1; b < lines.length; b++) {
                tiles[a - b][b] = new Tile(TileType.tileType(lines[b]));;
            }

            currentLength++;
            a++;
        }
        currentLength -= 2;
        a--;
        // decreasing part of the algorithm
        int k = 1;
        while (currentLength > 0) {
 
            String lines[] = nextLine();
            if (lines.length == 0) {
                continue;
            }
            if (lines.length != currentLength) {
                logger.warn("(down) Error: expected this line to have length " + currentLength + ", but got " + lines.length + ".");
                continue;
            }

            for (int i = 0; i < lines.length; i++) {
                tiles[a - i][i + k] = new Tile(TileType.tileType(lines[i]));
            }
            k++;
            currentLength--;
        }
        
        for(int i = 0; i < tiles.length; i++) {
            for(int j = 0; j < tiles.length; j++) {
                tiles[i][j].coords = new Vector2d(i, j);
            }
        }
        
        
        return tiles;
    }

    private String[] nextLine() {
        String line = this.scanner.nextLine();
        line = line.replaceAll("[\\s_/\\\\]", "");
        if (line.equals("")) {
            ignoredLines++;
            return new String[0];
        } 
        return line.split("");
    }
}
