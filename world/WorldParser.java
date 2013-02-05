import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;

public class WorldParser{
    private String file;
    private int dimensions;
    private String description;
    private int players;
    private int ignoredLines = 0;
	
    public WorldParser(String filename){
	file = filename;
    }
    public World parseFile() throws FileNotFoundException {
	System.out.println("[MAPPARSE] Parsing map '" + file + "'");
	Scanner scanner = new Scanner(new File(file));
	parseHeader(scanner);
	System.out.println("[MAPPARSE] Players: " + players);
	System.out.println("[MAPPARSE] size: " + dimensions);
	System.out.println("[MAPPARSE] description: '" + description + "'");
	Tile topCorner = parseBody(scanner);
	System.out.println("[MAPPARSE] Done parsing.");
	return new World(topCorner);
    }
    private void parseHeader(Scanner scanner){
	String playersArray[] = scanner.nextLine().split("\\s");
	String sizeArray[] = scanner.nextLine().split("\\s");
	String descriptionArray[] = scanner.nextLine().split("\\s");
	
	
	int numPlayers = Integer.parseInt(playersArray[1]);
	assert(numPlayers <= 8);
	assert(numPlayers > 1);
	int dimensionsInteger = Integer.parseInt(sizeArray[1]);
	assert(dimensionsInteger >= 2);
	assert(dimensionsInteger < 100);

	StringBuilder descriptionBuilder = new StringBuilder(descriptionArray[1]);
	for(int i = 2; i < descriptionArray.length; i++){
	    descriptionBuilder.append(" " + descriptionArray[i]);
	}
	String finalDescription
	    = descriptionBuilder.toString().substring(1, descriptionBuilder.length() - 1);
	players = numPlayers;
	description = finalDescription;
	dimensions = dimensionsInteger;
    }

    private Tile parseBody(Scanner scanner){
	Tile rootTile = null;
	int currentLength = 1;
	int i = 0;
	// increasing part of the algorithm
	while(i < dimensions){
	    String lines[] = getScannedLine(scanner);
	    if(lines.length == 0){
		continue;
	    }
	    if(lines.length != currentLength){
		System.out.println("[MAPPARSE] Error: expected this line to have length "
				   + currentLength + ", but got " + lines.length);
		continue;
	    }
	    if(currentLength == 1){
		rootTile = new Tile(lines[0]);
	    }
	    else {
		Tile currentTile = rootTile;
		while(currentTile.leftDown != null){
		    currentTile = currentTile.leftDown;
		}
		currentTile.leftDown = new Tile(lines[0]);
		currentTile.leftDown.rightUp = currentTile;
		
		for(int j = 1; j < lines.length; j++){
		    Tile newTile = new Tile(lines[j]);
		    currentTile.rightDown = newTile;
		    newTile.leftUp = currentTile;
		    if(currentTile.rightUp != null){
			newTile.up = currentTile.rightUp;
			currentTile.rightUp.down = newTile;
		    }
		    if((currentTile != rootTile) && (j != lines.length - 1)){
			currentTile = currentTile.rightUp.rightDown;
			currentTile.leftDown = newTile;
			newTile.rightUp = currentTile;
		    }
		}
	    }
	    
	    currentLength++;
	    i++;
	}
	currentLength -= 2;
	// decreasing part of the algorithm
	System.out.println("descending");
	Tile cornerTile = rootTile;
	cornerTile = rootTile;
	while(cornerTile.leftDown != null){
	    cornerTile = cornerTile.leftDown;
	}
	Tile lowerCornerTile = cornerTile;
	while(currentLength > 0){
	    lowerCornerTile = cornerTile;
	    while(lowerCornerTile.rightDown != null){
		lowerCornerTile = lowerCornerTile.rightDown;
	    }
	    String lines[] = getScannedLine(scanner);
	    if(lines.length == 0){
		continue;
		// TODO register skipped lines
	    }
	    if(lines.length != currentLength){
		System.out.println("[MAPPARSE] (down) Error: expected this line to have length "
				   + currentLength + ", but got " + lines.length);
		continue;
	    }
	    Tile currentTile = lowerCornerTile;
	    int tiles = 0;
	    for(String tileType: lines){
		tiles++;
		Tile newTile = new Tile(tileType);
		currentTile.rightDown = newTile;
		currentTile.rightDown.leftUp = currentTile;
		newTile.up = currentTile.rightUp;
		currentTile.rightUp.down = newTile;

		currentTile = currentTile.rightUp.rightDown;
		newTile.rightUp = currentTile;
		currentTile.leftDown = newTile;
	    }
	    System.out.println("put " + tiles + " tiles into row");
	    currentLength--;
	}
	return rootTile;
    }

    private String[] getScannedLine(Scanner scanner){
	String line = scanner.nextLine();
	line = line.replace("/", "");
	line = line.replace("\\", "");
	line = line.replace("_", "");
	line = line.replace(" ", "");
	line = line.replace("\t", "");
	if(line.equals("")){
	    System.out.println("ignored empty line");
	    return new String[0];
	}
	String array[] = line.split("");
	String newArray[] = new String[array.length-1];
	System.arraycopy(array, 1, newArray, 0, array.length-1); // oh java, you so silly
	return newArray;
    }
}
