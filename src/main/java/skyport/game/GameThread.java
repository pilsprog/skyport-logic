package skyport.game;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import skyport.debug.Debug;
import skyport.exception.ProtocolException;
import skyport.message.action.ActionMessage;
import skyport.network.ai.AIConnection;
import skyport.network.graphics.GraphicsContainer;

public class GameThread {
    ConcurrentLinkedQueue<AIConnection> globalClients;
    int minUsers;
    int gameTimeoutSeconds;
    public int roundTimeMilliseconds;
    boolean accelerateDeadPlayers = true; // TODO: set this to 'false' to be
    // more compliant with spec
    World world;
    PlayerSelector playerSelector;
    AtomicInteger readyUsers = new AtomicInteger(0); // for loadouts
    GraphicsContainer graphicsContainer = null;

    public GameThread(ConcurrentLinkedQueue<AIConnection> globalClientsArg, 
            int minUsersArg, int gameTimeoutSecondsArg, 
            int roundTimeMillisecondsArg, World worldArg, 
            GraphicsContainer graphicsContainerArg) {
        graphicsContainer = graphicsContainerArg;
        world = worldArg;
        globalClients = globalClientsArg;
        minUsers = minUsersArg;
        gameTimeoutSeconds = gameTimeoutSecondsArg;
        roundTimeMilliseconds = roundTimeMillisecondsArg;
    }

    public void run(int gameSecondsTimeout) {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
        }
        Debug.info("waiting for graphics engine to connect");
        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            if (graphicsContainer.get() != null) {
                break;
            }
        }
        graphicsContainer.get().thinktime = roundTimeMilliseconds;
        Debug.info("got graphics engine connection");
        Debug.info("waiting for " + minUsers + " users to connect");

        while (true) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
            if (globalClients.size() == minUsers) {
                Debug.info("Got " + minUsers + " users, starting the round");
                break;
            }
        }
        Debug.debug("Initializing game");
        gameMainLoop();
    }

    public void gameMainLoop() {
        Debug.debug("All clients connected.");
        initializeBoardWithPlayers();
        playerSelector = new PlayerSelector(globalClients);
        Debug.info("Sending initial gamestart packet");
        sendGamestate(0);
        // we just loop until everyone has selected a loadout.
        while (true) {
            boolean allAreReady = true;
            for (AIConnection conn : globalClients) {
                if (!conn.gotLoadout.get()) {
                    Debug.info("Waiting for loadout from " + conn.getPlayer().name);
                    allAreReady = false;
                }
            }
            if (allAreReady) {
                break;
            }

            letClientsThink();
            // letClientsThink();
            // letClientsThink();
        }
        sendDeadline();
        graphicsContainer.get().sendEndActions();
        graphicsContainer.get().waitForGraphics();
        Debug.debug("All clients have sent a loadout");
        long startTime = System.nanoTime();
        long gtsAsLong = gameTimeoutSeconds;
        int roundNumber = 1;

        boolean sixtyMarker = false;
        boolean thirtyMarker = false;
        boolean twentyMarker = false;
        boolean tenMarker = false;

        while (true) {
            Debug.printGamestats(globalClients);
            int playerNum = world.verifyNumberOfPlayersOnBoard();
            if (playerNum != globalClients.size()) {
                Debug.warn(globalClients.size() + " players are supposed to be" +
                           " on the field, but found " + playerNum + 
                           ". Possible inconsistency during movement?");
            }

            long roundStartTime = System.nanoTime();
            double timeLeft = (gtsAsLong - ((roundStartTime - startTime) / 1000000000.0));
            if (timeLeft < 60 && !sixtyMarker) {
                Debug.guiMessage("60 seconds left!");
                sixtyMarker = true;
            }
            if (timeLeft < 30 && !thirtyMarker) {
                Debug.guiMessage("30 seconds left!");
                thirtyMarker = true;
            }
            if (timeLeft < 20 && !twentyMarker) {
                Debug.guiMessage("20 seconds left!");
                twentyMarker = true;
            }
            if (timeLeft < 10 && !tenMarker) {
                Debug.guiMessage("10 seconds left!");
                tenMarker = true;
            }
            if ((roundStartTime - startTime) > gtsAsLong * 1000000000) {
                Debug.info("Time over!");
                int highestScore = -1000000000;
                String winningPlayer = "";
                for (AIConnection connection : globalClients) {
                    if (connection.getPlayer().score > highestScore) {
                        highestScore = connection.getPlayer().score;
                        winningPlayer = connection.getPlayer().name;
                    }
                }
                String wintext = winningPlayer + " wins!";
                for (AIConnection connection : globalClients) {
                    if (connection.getPlayer().score == highestScore && !winningPlayer.equals(connection.getPlayer().name)) {
                        wintext = "Tie!";
                    }
                }
                while (true) {
                    Debug.guiMessage("Time over!");
                    letClientsThink();
                    Debug.guiMessage(wintext);
                    letClientsThink();
                }
                // System.exit(0);
            }
            AIConnection currentPlayer = sendGamestate(roundNumber);
            Debug.marker("START TURN " + roundNumber + " PLAYER: '" + currentPlayer.getPlayer().name + "'");
            if (currentPlayer.isAlive() || !accelerateDeadPlayers) {
                letClientsThink();
            } else {
                Debug.debug("Player '" + currentPlayer.getPlayer().name +
                          "' is dead and accelerateDeadPlayers flag is set, sending" + " deadline immediately...");
            }
            sendDeadline();
            Debug.debug("Deadline! Processing actions...");
            processThreePlayerActions(currentPlayer);
            givePenalityForLingeringOnSpawntile(currentPlayer);
            graphicsContainer.get().sendEndActions();
            graphicsContainer.get().waitForGraphics();
            syncWithGraphics();
            roundNumber++;
        }

    }

    private void givePenalityForLingeringOnSpawntile(AIConnection currentPlayer) {
        if (currentPlayer.getPlayer().position == currentPlayer.getPlayer().spawnTile) {
            Debug.warn("Player " + currentPlayer.getPlayer().name + " stayed on spawn too long");
            currentPlayer.givePenality(10);
        }
    }

    private void processThreePlayerActions(AIConnection currentPlayer) {
        List<ActionMessage> actions = Arrays.asList(
                currentPlayer.getNextMessage(),
                currentPlayer.getNextMessage(),
                currentPlayer.getNextMessage());
        int validActions = 0;
        int a = 3;
        for(ActionMessage action : actions) {
            if(letPlayerPerformAction(action, currentPlayer, a--)) {
                broadcastAction(action, currentPlayer);
                validActions++;
                if (Util.wasActionOffensive(action)) {
                    return;
                }
            } else {
                Debug.debug("Action "+a+" was invalid");
            }
        }
        Debug.game("player " + currentPlayer.getPlayer().name + " performed " + validActions + " valid actions.");
        if (validActions == 0) {
            currentPlayer.givePenality(10);
        }
    }

    private void broadcastAction(ActionMessage action, AIConnection playerWhoPerformedTheAction) {
        Debug.debug("Action was valid, re-broadcasting (FIXME)");
        action.setFrom(playerWhoPerformedTheAction.getPlayer().name);
        System.out.println("ACTION: " + action.toString());

        graphicsContainer.get().sendMessage(action);
        for (AIConnection player : globalClients) {
            player.sendMessage(action);
        }
    }

    private boolean letPlayerPerformAction(ActionMessage action, AIConnection currentPlayer, int turnsLeft) {
        if (action == null) {
            return false;
        }
        currentPlayer.getPlayer().setTurnsLeft(turnsLeft);
        try {
            return action.performAction(currentPlayer.getPlayer());
        } catch (ProtocolException e) {
            currentPlayer.sendError(e.getMessage());
            return false;
        }
    }

    public void syncWithGraphics() {
        int newRoundTime = graphicsContainer.get().thinktime;
        if (newRoundTime != roundTimeMilliseconds) {
            roundTimeMilliseconds = newRoundTime;
            Debug.info("Delay changed to " + newRoundTime);
            Debug.guiMessage("Delay changed to " + newRoundTime);
        }
    }

    public void letClientsThink() {
        try {
            Thread.sleep(roundTimeMilliseconds); // 1000
        } catch (InterruptedException e) {
            Debug.warn("INTERRUPTED!");
        }
    }

    public AIConnection sendGamestate(int round) {
        AIConnection playerTurnOrder[] = playerSelector.getListInTurnOrderAndMoveToNextTurn();
        TileType matrix[][] = world.returnAsRowMajorMatrix();

        GameMap map = new GameMap(world.getJLength(), world.getKLength(), matrix);

        if (round != 0) {
            playerTurnOrder[0].clearAllMessages();
        }
        graphicsContainer.get().sendGamestate(round, map, playerTurnOrder);
        for (AIConnection client : globalClients) {
            client.sendGamestate(round, map, playerTurnOrder);
        }
        return playerTurnOrder[0];
    }

    public void sendDeadline() {
        graphicsContainer.get().sendDeadline();
        for (AIConnection client : globalClients) {
            client.sendDeadline();
        }
    }

    public void initializeBoardWithPlayers() {
        // Associate AIs with players
        for (AIConnection client : globalClients) {
            Tile spawn = world.getRandomSpawnpoint();
            client.setSpawnpoint(spawn);
        }
    }
}
