package skyport.game;

import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.concurrent.ConcurrentLinkedQueue;

import skyport.debug.Debug;
import skyport.network.ai.AIConnection;

class RingNode {
    public AIConnection connection;
    public RingNode next;
    public RingNode prev;

    public RingNode(AIConnection thisConnectionArg) {
        connection = thisConnectionArg;
    }
}

public class PlayerSelector {
    RingNode rootNode; // NB: This will get outdated if a client disconnects
    RingNode currentPlayer;
    ConcurrentLinkedQueue<AIConnection> updatedGlobalList;

    public PlayerSelector(ConcurrentLinkedQueue<AIConnection> playerList) {
        LinkedList<AIConnection> globalClients = new LinkedList<AIConnection>();
        updatedGlobalList = playerList;

        for (AIConnection connection : playerList) {
            globalClients.add(connection);
        }
        Collections.shuffle(globalClients);
        rootNode = new RingNode(globalClients.getFirst());
        RingNode currentNode = rootNode;
        ListIterator<AIConnection> iter = globalClients.listIterator(1);
        while (iter.hasNext()) {
            RingNode newNode = new RingNode(iter.next());
            currentNode.next = newNode;
            newNode.prev = currentNode;
            currentNode = newNode;
        }
        currentNode.next = rootNode;
        rootNode.prev = currentNode;
        currentPlayer = currentNode;
    }

    public AIConnection[] getListInTurnOrderAndMoveToNextTurn() {
        // TODO: Dead players are currently not purged anymore. Verify that
        // works...
        // purgeDeadPlayersFromRing();

        // TODO: build in check here to skip over passing players
        // TODO: test this code
        while (true) {
            currentPlayer = currentPlayer.next;
            if (currentPlayer.connection.hasToPass) {
                currentPlayer.connection.hasToPass = false;
            } else {
                // player doesn't have to pass his turn, but he hasn't respawned
                // yet. Respawn him.
                if (currentPlayer.connection.needsRespawn && !currentPlayer.connection.hasToPass) {
                    currentPlayer.connection.respawn();
                }
                break; // we found a player who doesn't have to pass his turn
            }
        }

        LinkedList<AIConnection> templist = new LinkedList<AIConnection>();
        RingNode tempPlayer = currentPlayer; // it's currentPlayers turn.
        do {
            templist.add(tempPlayer.connection);
            tempPlayer = tempPlayer.next;
        } while (tempPlayer != currentPlayer);

        AIConnection[] connectionArrayWorkaroundForJavasStupidTypeSystem = new AIConnection[0];
        return templist.toArray(connectionArrayWorkaroundForJavasStupidTypeSystem);
    }

    public void purgeDeadPlayersFromRing() {
        RingNode tempPlayer = currentPlayer;
        do {
            if (!tempPlayer.connection.isAlive()) {
                if (tempPlayer.next == tempPlayer) {
                    Debug.error("Last player disconnected, press enter to quit");
                }
                Debug.warn("Found dead player '" + tempPlayer.connection.getPlayer() + "' in ring, purging...");
                tempPlayer.prev.next = tempPlayer.next;
                if (tempPlayer == currentPlayer) {
                    currentPlayer = currentPlayer.next;
                }
            }
            tempPlayer = tempPlayer.next;
        } while (tempPlayer != currentPlayer);
    }
}
