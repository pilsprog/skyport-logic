import java.util.concurrent.ConcurrentLinkedQueue;
import java.io.FileNotFoundException;

public class Main {
    static int roundTimeSeconds = 3;
    public static void main(String[] args) {
        int port = 54321;
	int minUsers = 2;
	int gameTimeoutSeconds = 600;
	String mapfile = "";
	try {
	    assert(args.length == 4);
	    port = Integer.parseInt(args[0]);
	    minUsers = Integer.parseInt(args[1]);
	    gameTimeoutSeconds = Integer.parseInt(args[2]);
	    mapfile = args[3];
	}
	catch (Exception e){
	    System.out.println("Usage: ./run.sh <port> <number of users> <game time> <mapfile>");
	    System.exit(1);	    
	}

	try {
	    WorldParser wp = new WorldParser(mapfile);
	    wp.parseFile();
	}
	catch(FileNotFoundException e){
	    System.out.println("File not found: '" + mapfile + "'");
	}
	
	ConcurrentLinkedQueue<AIConnection> globalClientList = new ConcurrentLinkedQueue<AIConnection>();
        Acceptor aiClientAcceptor = new Acceptor(port, globalClientList, minUsers, false);
	new Thread(aiClientAcceptor).start();

	Acceptor graphicsClientAcceptor = new Acceptor(port+10, null, 1, true);
	graphicsClientAcceptor.run();
	

	// the main thread simply becomes the new gamethread.
	GameThread game = new GameThread(globalClientList, minUsers, gameTimeoutSeconds, roundTimeSeconds);
	game.run(gameTimeoutSeconds);
    }
}
