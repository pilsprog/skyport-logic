import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String args[]) {
        int port = 54321;
	int minUsers = 2;
	int gameTimeoutSeconds = 600;
	int aiThinkTimeout = 3000;
	String mapfile = "";
	try {
	    assert(args.length == 4 || args.length == 5);
	    port = Integer.parseInt(args[0]);
	    minUsers = Integer.parseInt(args[1]);
	    gameTimeoutSeconds = Integer.parseInt(args[2]);
	    mapfile = args[3];
	    if(args.length == 5){
		aiThinkTimeout = Integer.parseInt(args[4]);
		System.out.println("Using an AI think timeout of " + aiThinkTimeout + "ms for each turn");
	    }
	    else {
		System.out.println("Using default AI think timeout of " + aiThinkTimeout + "ms for each turn");
	    }
	}
	catch (Exception e){
	    System.out.println("Usage: ./server <port> <number of users> <game time> <mapfile> [think-timeout in milliseconds]");
	    System.exit(1);	    
	}
	World world = null;
	int spawnPoints = 0;
	try {
	    WorldParser wp = new WorldParser(mapfile);
	    world = wp.parseFile();
	    spawnPoints = world.getSpawnpointNumber();
	    if(spawnPoints < minUsers){
		System.out.println("Error: requested to wait for " + minUsers
				   + " AIs, but this map only supports " + spawnPoints + ".");
		System.exit(1);
	    }
	    if(spawnPoints > minUsers){
		System.out.println("Warning: playing with " + minUsers + " on a map for " + spawnPoints
				   + " users, gameplay may be unbalanced.");
	    }
	}
	catch(FileNotFoundException e){
	    System.out.println("File not found: '" + mapfile + "'");
	    System.exit(1);
	}
	
	ConcurrentLinkedQueue<AIConnection> globalClientList = new ConcurrentLinkedQueue<AIConnection>();
	GraphicsContainer graphicsContainer = new GraphicsContainer();
        Acceptor aiClientAcceptor = new Acceptor(port, globalClientList, minUsers, false, null);
	new Thread(aiClientAcceptor).start();

	Acceptor graphicsClientAcceptor = new Acceptor(port+10, null, 1, true, graphicsContainer);
	new Thread(graphicsClientAcceptor).start();
	

	// the main thread simply becomes the new gamethread.
	System.out.println("Starting gamethread...");
	GameThread game =
	    new GameThread(globalClientList, minUsers, gameTimeoutSeconds,
			   aiThinkTimeout, world, graphicsContainer);
	game.run(gameTimeoutSeconds);
    }
}
