import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class WorldParser{
    private String file;
    public WorldParser(String filename){
	file = filename;
    }
    public void parseFile() throws FileNotFoundException {
	System.out.println("[MAPPARSE] Parsing map '" + file + "'");
	Scanner scanner = new Scanner(new File(file));
	parseHeader(scanner);
	System.out.println("[MAPPARSE] Done parsing.");
    }
    public void parseHeader(Scanner scanner){
	String players[] = scanner.nextLine().split("\\s");
	String size[] = scanner.nextLine().split("\\s");
	String description[] = scanner.nextLine().split("\\s");
	description[0] = "";
	
	int numPlayers = Integer.parseInt(players[1]);
	assert(numPlayers <= 8);
	assert(numPlayers > 1);
	int dimensions = Integer.parseInt(size[1]);
	assert(dimensions >= 2);
	assert(dimensions < 100);

	StringBuilder descriptionBuilder = new StringBuilder();
	for(String elem: description){
	    descriptionBuilder.append(" " + elem);
	}

	System.out.println("[MAPPARSE] Players: " + numPlayers);
	System.out.println("[MAPPARSE] size: " + dimensions);
	System.out.println("[MAPPARSE] description: '" + descriptionBuilder + "'");
    }
}
