import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

class RingNode {
    public AIConnection connection;
    public RingNode next;
    public RingNode prev;
    public RingNode(AIConnection thisConnectionArg){
	connection = thisConnectionArg;
    }
}

public class PlayerSelector {
    RingNode rootNode; // NB: This will get outdated if a client disconnects
    RingNode currentPlayer;
    ConcurrentLinkedQueue<AIConnection> updatedGlobalList;
    
    public PlayerSelector(ConcurrentLinkedQueue<AIConnection> playerList){
	LinkedList<AIConnection> globalClients = new LinkedList<AIConnection>();
	updatedGlobalList = playerList;
	
	for(AIConnection connection : playerList){
	    globalClients.add(connection);
	}
	Collections.shuffle(globalClients);
	rootNode = new RingNode(globalClients.getFirst());
	RingNode currentNode = rootNode;
	ListIterator<AIConnection> iter = globalClients.listIterator(1);
	while(iter.hasNext()){
	    RingNode newNode = new RingNode(iter.next());
	    currentNode.next = newNode;
	    newNode.prev = currentNode;
	    currentNode = newNode;
	}
	currentNode.next = rootNode;
	rootNode.prev = currentNode;
	currentPlayer = currentNode;
    }
    
    public AIConnection[] getListInTurnOrderAndMoveToNextTurn(){
	// TODO: Dead players are currently not purged anymore. Verify that works...
	// purgeDeadPlayersFromRing();

	// TODO: build in check here to skip over passing players
	// TODO: test this code
	while(true){
	    currentPlayer = currentPlayer.next;
	    if(currentPlayer.connection.health == 0){
		currentPlayer.connection.respawn(); // fill up health and teleport to spawn
		// player will get a turn again next round
	    }
	    else {
		break; // we found a player who doesn't have to pass his turn
	    }
	}
	
	LinkedList<AIConnection> templist = new LinkedList<AIConnection>();
	RingNode tempPlayer = currentPlayer; // it's currentPlayers turn.
	do {
	    templist.add(tempPlayer.connection);
	    tempPlayer = tempPlayer.next;
	} while(tempPlayer != currentPlayer);

	
	AIConnection[] connectionArrayWorkaroundForJavasStupidTypeSystem = new AIConnection[0];
	return templist.toArray(connectionArrayWorkaroundForJavasStupidTypeSystem);
    }
    public void purgeDeadPlayersFromRing(){
	RingNode tempPlayer = currentPlayer;
	do {
	    if(!tempPlayer.connection.isAlive){
		if(tempPlayer.next == tempPlayer){
		    Util.pressEnterToContinue("Last player disconnected, press enter to quit");
		    System.exit(1);
		}
		System.out.println("Found dead player '" + tempPlayer.connection.username
				   + "' in ring, purging...");
		tempPlayer.prev.next = tempPlayer.next;
		if(tempPlayer == currentPlayer){
		    currentPlayer = currentPlayer.next;
		}
	    }
	    tempPlayer = tempPlayer.next;
	} while(tempPlayer != currentPlayer);
    }
}
