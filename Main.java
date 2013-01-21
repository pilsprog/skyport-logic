import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
    static int roundTimeSeconds = 3;
    public static void main(String[] args) {
        int port = 54321;
	int minUsers = 2;
	int gameTimeoutSeconds = 600;
	try {
	    assert(args.length == 3);
	    port = Integer.parseInt(args[0]);
	    minUsers = Integer.parseInt(args[1]);
	    gameTimeoutSeconds = Integer.parseInt(args[2]);
	}
	catch (Exception e){
	    System.out.println("Usage: ./run.sh <port> <number of users> <game time>");
	    System.exit(1);	    
	}

	ConcurrentLinkedQueue<AIConnection> globalClientList = new ConcurrentLinkedQueue<AIConnection>();
        Acceptor aiClientAcceptor = new Acceptor(port, globalClientList);
	new Thread(aiClientAcceptor).start();

	// the main thread simply becomes the new gamethread.
	GameThread game = new GameThread(globalClientList, minUsers, gameTimeoutSeconds, roundTimeSeconds);
	game.run(gameTimeoutSeconds);
    }
}
