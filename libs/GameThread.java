import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.*;
/*
 * Program flow:
 *
 * 1) Load world
 * 2) Start acceptors
 * 3) Wait for the given number of players to connect
 * 4) Randomize spawnpoints on the board
 * 4) Send initial GAMESTART
 * 5) Wait for players to select loadout
 * 6) Start ticking
 *
 */

public class GameThread {
    ConcurrentLinkedQueue<AIConnection> globalClients;
    int minUsers;
    int gameTimeoutSeconds;
    int roundTimeSeconds;
    GameState gamestate;
    World world;
    PlayerSelector playerSelector;
    AtomicInteger readyUsers = new AtomicInteger(0); // for loadouts
    public GameThread(ConcurrentLinkedQueue<AIConnection> globalClientsArg,
		      int minUsersArg, int gameTimeoutSecondsArg, int roundTimeSecondsArg,
		      World worldArg){
	world = worldArg;
	globalClients = globalClientsArg;
	minUsers = minUsersArg;
	gameTimeoutSeconds = gameTimeoutSecondsArg;
	roundTimeSeconds = roundTimeSecondsArg;
	//	gamestate = new GameState("testworld.skyportmap");
    }
    public void run(int gameSecondsTimeout){
	try {
	    Thread.sleep(500);
	}
	catch(InterruptedException e){}
	System.out.println("[GAMETHRD] waiting for " + minUsers + " users to connect");
	int waitIteration = 0;
	while(true){
	    waitIteration++;
	    try {
		Thread.sleep(500);
	    }
	    catch(InterruptedException e){}
	    if(globalClients.size() == minUsers){
		System.out.println("[GAMETHRD] Got " + minUsers + " users, starting the round");
		break;
	    }
	}
	for(AIConnection conn: globalClients){
	    JSONObject o = conn.getNextMessage();
	    System.out.println("[GAMETHRD] stub: got new message");
	}
	System.out.println("[GAMETHRD] Initializing game");
	gameMainloop();
    }
    public void gameMainloop(){
	System.out.println("All clients connected.");
	Util.pressEnterToContinue("Press enter to randomize spawns and send the initial gamestate");
	initializeBoardWithPlayers();
	playerSelector = new PlayerSelector(globalClients);
	System.out.println("[GAMETHRD] Sending initial gamestate");
	sendGamestate(0);
	// we just loop until everyone has selected a loadout.
	while(true){
	    boolean allAreReady = true;
	    for(AIConnection conn: globalClients){
		if(!conn.gotLoadout.get()){
		    System.out.println("Waiting for loadout from " + conn.username);
		    allAreReady = false;
		}
	    }
	    if(allAreReady){
		break;
	    }
	    letClientsThink();
	    letClientsThink();
	    letClientsThink();
	}
	sendDeadline();
	System.out.println("All clients have sent a loadout");
	Util.pressEnterToContinue("Press enter to start the game");
	long startTime = System.nanoTime();
	long gtsAsLong = gameTimeoutSeconds;
	int roundNumber = 1;
	while(true){
	    long roundStartTime = System.nanoTime();
	    if((roundStartTime - startTime) > gtsAsLong*1000000000){
		System.out.println("[GAMETHRD] Time over!");
		System.exit(0);
	    }
	    System.out.println("[GAMETHRD] Sending gamestate...");
	    // TODO: clear out message queue of the player whos turn it is
	    sendGamestate(roundNumber);
	    letClientsThink();
	    sendDeadline();
	    // simulate the GUI working -- we will have to wait for it later
	    letClientsThink();
	    letClientsThink();
	    // end GUI working
	    System.out.println("[GAMETHRD] Deadline! Processing actions...");
	    // processing actions here

	    roundNumber++;
	}
	
    }
    public void letClientsThink(){
	try {
	    Thread.sleep(roundTimeSeconds*1000);
	}
	catch (InterruptedException e){
	    System.out.println("INTTERUPTED!");
	}
    }
    public void sendGamestate(int roundNumber){
	// TODO: visualization needs to be integrated here
	String matrix[][] = world.returnAsRowMajorMatrix();
	AIConnection playerTurnOrder[] = playerSelector.getListInTurnOrderAndMoveToNextTurn();
	playerTurnOrder[0].giveNewMoves();
	for(AIConnection client: globalClients){
	    client.sendGamestate(roundNumber, world.dimension, matrix, playerTurnOrder);
	}
    }
    public void sendDeadline(){
	for(AIConnection client: globalClients){
	    client.sendDeadline();
	    client.resetMoves();
	}
    }
    public void initializeBoardWithPlayers(){
	// Associate AIs with players
	for(AIConnection client: globalClients){
	    Tile spawn = world.getRandomSpawnpoint();
	    client.setSpawnpoint(spawn);
	}
    }
}
