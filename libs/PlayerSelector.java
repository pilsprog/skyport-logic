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
	System.out.println("PlayerSelector.java:20: TODO: implement disappearing clients");
	// when a client disappears, we need to remove it from the ringbuffer 
	// without otherwise disturbing the turn order.
	LinkedList<AIConnection> templist = new LinkedList<AIConnection>();
	RingNode tempPlayer = currentPlayer;
	do {
	    templist.add(tempPlayer.connection);
	    tempPlayer = tempPlayer.next;
	} while(tempPlayer != currentPlayer);
	
	currentPlayer = currentPlayer.next;
	AIConnection[] connectionArrayWorkaroundForJavasStupidTypeSystem = new AIConnection[0];
	return templist.toArray(connectionArrayWorkaroundForJavasStupidTypeSystem);
    }
}
