package skyport.game;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorldParser {
    private String file;
    private int jLength;
    private int kLength;
    private String description;
    private int players;
    private int ignoredLines = 0;
    
    private final Logger logger = LoggerFactory.getLogger(WorldParser.class);

    public WorldParser(String filename) {
        file = filename;
    }

    public World parseFile() throws FileNotFoundException {
        logger.info("Parsing map '" + file + "'");
        Scanner scanner = new Scanner(new File(file));
        parseHeader(scanner);
        logger.info("Players: " + players);
        logger.info("size: " + jLength);
        logger.info("description: '" + description + "'");
        Tile[][] tiles = parseBody(scanner);
        logger.debug("Done parsing. Ignored " + ignoredLines + " empty lines.");
        return new World(tiles, file, jLength);
    }

    private void parseHeader(Scanner scanner) {
        String playersArray[] = scanner.nextLine().split("\\s");
        String sizeArray[] = scanner.nextLine().split("\\s");
        String descriptionArray[] = scanner.nextLine().split("\\s");

        int numPlayers = Integer.parseInt(playersArray[1]);
        assert (numPlayers <= 8);
        assert (numPlayers > 1);
        int dimensionsInteger = Integer.parseInt(sizeArray[1]);
        assert (dimensionsInteger >= 2);
        assert (dimensionsInteger < 100);

        StringBuilder descriptionBuilder = new StringBuilder(descriptionArray[1]);
        for (int i = 2; i < descriptionArray.length; i++) {
            descriptionBuilder.append(" " + descriptionArray[i]);
        }
        String finalDescription = descriptionBuilder.toString().substring(1, descriptionBuilder.length() - 1);
        players = numPlayers;
        description = finalDescription;
        jLength = dimensionsInteger;
        kLength = dimensionsInteger;
    }

    private Tile[][] parseBody(Scanner scanner) {
        Tile rootTile = null;
        int currentLength = 1;
        int a = 0;

        Tile[][] tiles = new Tile[jLength][];
        for (int j = 0; j < tiles.length; j++) {
            tiles[j] = new Tile[kLength];
        }

        // increasing part of the algorithm
        while (a < jLength) {
            String lines[] = getScannedLine(scanner);
            if (lines.length == 0) {
                continue;
            }
            if (lines.length != currentLength) {
                logger.warn("Error: expected this line to have length " + currentLength + ", but got " + lines.length);
                continue;
            }
            if (currentLength == 1) {
                rootTile = new Tile(lines[0]);
                tiles[a][0] = rootTile;
            } else {
                Tile currentTile = rootTile;
                while (currentTile.leftDown != null) {
                    currentTile = currentTile.leftDown;
                }
                currentTile.leftDown = new Tile(lines[0]);
                currentTile.leftDown.rightUp = currentTile;
                tiles[a][0] = currentTile.leftDown;

                for (int b = 1; b < lines.length; b++) {
                    Tile newTile = new Tile(lines[b]);
                    tiles[a - b][b] = newTile;
                    currentTile.rightDown = newTile;
                    newTile.leftUp = currentTile;
                    if (currentTile.rightUp != null) {
                        newTile.up = currentTile.rightUp;
                        currentTile.rightUp.down = newTile;
                    }
                    if ((currentTile != rootTile) && (b != lines.length - 1)) {
                        currentTile = currentTile.rightUp.rightDown;
                        currentTile.leftDown = newTile;
                        newTile.rightUp = currentTile;
                    }
                }
            }

            currentLength++;
            a++;
        }
        currentLength -= 2;
        a--;
        // decreasing part of the algorithm
        Tile cornerTile = rootTile;
        cornerTile = rootTile;
        while (cornerTile.leftDown != null) {
            cornerTile = cornerTile.leftDown;
        }
        Tile lowerCornerTile = cornerTile;
        int k = 1;
        while (currentLength > 0) {
            lowerCornerTile = cornerTile;
            while (lowerCornerTile.rightDown != null) {
                lowerCornerTile = lowerCornerTile.rightDown;
            }
            String lines[] = getScannedLine(scanner);
            if (lines.length == 0) {
                continue;
            }
            if (lines.length != currentLength) {
                logger.warn("(down) Error: expected this line to have length " + currentLength + ", but got " + lines.length);
                continue;
            }

            Tile currentTile = lowerCornerTile;
            for (int i = 0; i < lines.length; i++) {
                String tileType = lines[i];
                Tile newTile = new Tile(tileType);
                tiles[a - i][i + k] = newTile;
                currentTile.rightDown = newTile;
                currentTile.rightDown.leftUp = currentTile;
                newTile.up = currentTile.rightUp;
                currentTile.rightUp.down = newTile;

                currentTile = currentTile.rightUp.rightDown;
                newTile.rightUp = currentTile;
                currentTile.leftDown = newTile;
            }
            k++;
            currentLength--;
        }
        return tiles;
    }

    private String[] getScannedLine(Scanner scanner) {
        String line = scanner.nextLine();
        line = line.replaceAll("[ \t_/\\\\]", "");
        if (line.equals("")) {
            ignoredLines++;
            return new String[0];
        }
        String array[] = line.split("");
        String newArray[] = new String[array.length - 1];
        System.arraycopy(array, 1, newArray, 0, array.length - 1); // oh java,
        // you so
        // silly
        return newArray;
    }
}
