import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.*;
import java.io.IOException;

public class GameThread {
    ConcurrentLinkedQueue<AIConnection> globalClients;
    int minUsers;
    int gameTimeoutSeconds;
    int roundTimeMilliseconds;
    boolean accelerateDeadPlayers = true; // TODO: set this to 'false' to be more compliant with spec
    World world;
    PlayerSelector playerSelector;
    AtomicInteger readyUsers = new AtomicInteger(0); // for loadouts
    GraphicsContainer graphicsContainer = null;
    public GameThread(ConcurrentLinkedQueue<AIConnection> globalClientsArg,
		      int minUsersArg, int gameTimeoutSecondsArg, int roundTimeMillisecondsArg,
		      World worldArg, GraphicsContainer graphicsContainerArg){
	graphicsContainer = graphicsContainerArg;
	world = worldArg;
	globalClients = globalClientsArg;
	minUsers = minUsersArg;
	gameTimeoutSeconds = gameTimeoutSecondsArg;
	roundTimeMilliseconds = roundTimeMillisecondsArg;
    }
    public void run(int gameSecondsTimeout){
	try {
	    Thread.sleep(500);
	}
	catch(InterruptedException e){}
	Debug.info("waiting for graphics engine to connect");
	while(true){
	    try {
		Thread.sleep(100);
	    } catch (InterruptedException e){}
	    if(graphicsContainer.get() != null){
		break;
	    }
	}
	Debug.info("got graphics engine connection");
	Debug.info("waiting for " + minUsers + " users to connect");
	int waitIteration = 0;
	while(true){
	    waitIteration++;
	    try {
		Thread.sleep(500);
	    }
	    catch(InterruptedException e){}
	    if(globalClients.size() == minUsers){
		Debug.info("Got " + minUsers + " users, starting the round");
		break;
	    }
	}
	Debug.debug("Initializing game");
	gameMainloop();
    }
    public void gameMainloop(){
	Debug.debug("All clients connected.");
	Util.pressEnterToContinue("Press enter to randomize spawns and send the initial gamestate");
	initializeBoardWithPlayers();
	playerSelector = new PlayerSelector(globalClients);
	Debug.info("Sending initial gamestart packet");
	sendGamestate(0);
	// we just loop until everyone has selected a loadout.
	while(true){
	    boolean allAreReady = true;
	    for(AIConnection conn: globalClients){
		if(!conn.gotLoadout.get()){
		    Debug.info("Waiting for loadout from " + conn.username);
		    allAreReady = false;
		}
	    }
	    if(allAreReady) break;
	    
	    letClientsThink();
	    //letClientsThink();
	    //letClientsThink();
	}
	sendDeadline();
	graphicsContainer.get().sendEndActions();
	graphicsContainer.get().waitForGraphics();
	Debug.debug("All clients have sent a loadout");
	Util.pressEnterToContinue("Press enter to start the game");
	long startTime = System.nanoTime();
	long gtsAsLong = gameTimeoutSeconds;
	int roundNumber = 1;
	while(true){
	    int playerNum = world.verifyNumberOfPlayersOnBoard();
	    if(playerNum != globalClients.size()){
		Debug.warn(globalClients.size() + " players are supposed to be"
				   + " on the field, but found " + playerNum
				   + ". Possible inconsistency during movement?");
	    }
		    
	    long roundStartTime = System.nanoTime();
	    if((roundStartTime - startTime) > gtsAsLong*1000000000){
		Debug.info("Time over!");
		System.exit(0);
	    }
	    // TODO: clear out message queue of the player whos turn it is
	    // TODO: test everything with all levels of weapons -- so far
	    // only tested with lvl 1 weapons.
	    AIConnection currentPlayer = sendGamestate(roundNumber);
	    Debug.marker("START TURN " + roundNumber + " PLAYER: '" + currentPlayer.username + "'");
	    if(currentPlayer.isAlive || !accelerateDeadPlayers){
		letClientsThink();
	    }
	    else {
		Debug.debug("Player '" + currentPlayer.username
				   + "' is dead and accelerateDeadPlayers flag is set, sending"
				   + " deadline immediately...");
	    }
	    sendDeadline();
	    Debug.debug("Deadline! Processing actions...");
	    JSONObject first = currentPlayer.getNextMessage();
	    JSONObject second = currentPlayer.getNextMessage();
	    JSONObject third = currentPlayer.getNextMessage();
	    int validAction = 0;
	    if(letPlayerPerformAction(first, currentPlayer)){
		broadcastAction(first, currentPlayer);
		validAction++;
	    }
	    else {Debug.debug("First action was invalid.");}
	    if(letPlayerPerformAction(second, currentPlayer)){
		broadcastAction(second, currentPlayer);
		validAction++;
	    }
	    else {Debug.debug("Second action was invalid.");}
	    if(letPlayerPerformAction(third, currentPlayer)){
		broadcastAction(third, currentPlayer);
		validAction++;
	    }
	    else {Debug.debug("Third action was invalid.");}
	    Debug.game("player " + currentPlayer.username + " performed " + validAction + " valid actions");
	    graphicsContainer.get().sendEndActions();
	    graphicsContainer.get().waitForGraphics();
	    roundNumber++;
	}
	
    }
    private void broadcastAction(JSONObject action, AIConnection playerWhoPerformedTheAction){
	// TODO: AIs can use this to inject extranous JSON fields into other peoples
	// receiver stream. Write a function that sanitizes the attribute first before
	// sending them off again.
	Debug.debug("Action was valid, re-broadcasting (FIXME)");
	try {
	    action.put("from", playerWhoPerformedTheAction.username);
	}
	catch (JSONException e){
	}
	try {
	    graphicsContainer.get().sendMessage(action);
	}
	catch (IOException e) {
	    Debug.debug("Warning: failed to broadcast action to graphics");
	}
	for(AIConnection player: globalClients){
	    try {
		player.sendMessage(action);
	    }
	    catch (IOException e){
		Debug.debug("Warning: Failed to broadcast action to " + player.username);
	    }
	}
    }
    private boolean letPlayerPerformAction(JSONObject action, AIConnection currentPlayer){
	if(action == null) return false;
	try {
	    String actiontype = action.getString("type");
	    switch (actiontype){
	    case "move":
		return currentPlayer.doMove(action);
	    case "laser":
		if(currentPlayer.position.tileType == TileType.SPAWN){
		    Debug.game("Player attempted to shoot laser from spawn.");
		    return false;
		}
		return currentPlayer.shootLaser(action, graphicsContainer.get());
	    case "droid":
		if(currentPlayer.position.tileType == TileType.SPAWN){
		    Debug.game("Player attempted to shoot droid from spawn.");
		    return false;
		}
		return currentPlayer.shootDroid(action);
	    case "mortar":
		if(currentPlayer.position.tileType == TileType.SPAWN){
		    Debug.game("Player attempted to shoot mortar from spawn.");
		    return false;
		}
		return currentPlayer.shootMortar(action);
	    case "mine":
		TileType currentTileType = currentPlayer.position.tileType;
		if(currentTileType == TileType.RUBIDIUM
		   || currentTileType == TileType.EXPLOSIUM
		   || currentTileType == TileType.SCRAP){
		    return currentPlayer.mineResource();
		}
		else {
		    Debug.game("Player " + currentPlayer.username + " attempted to mine while not on a resource");
		    currentPlayer.sendError("Tried to mine while not on a resource tile!");
		    return false;
		}
	    default:
		currentPlayer.invalidAction(action);
		return false;
	    }
	}
	catch (JSONException e){ // TODO: send back error about missing type
	}
	return false;
    }
    public void letClientsThink(){
	try {
	    Thread.sleep(roundTimeMilliseconds); // 1000
	}
	catch (InterruptedException e){
	    Debug.warn("INTERUPTED!");
	}
    }
    public AIConnection sendGamestate(int roundNumber){
	// TODO: visualization needs to be integrated here
	String matrix[][] = world.returnAsRowMajorMatrix();
	AIConnection playerTurnOrder[] = playerSelector.getListInTurnOrderAndMoveToNextTurn();
	if(roundNumber != 0){
	    playerTurnOrder[0].clearAllMessages();
	}
	graphicsContainer.get().sendGamestate(roundNumber, world.dimension, matrix, playerTurnOrder);
	for(AIConnection client: globalClients){
	    client.sendGamestate(roundNumber, world.dimension, matrix, playerTurnOrder);
	}
	return playerTurnOrder[0];
    }
    public void sendDeadline(){
	graphicsContainer.get().sendDeadline();
	for(AIConnection client: globalClients){
	    client.sendDeadline();
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
