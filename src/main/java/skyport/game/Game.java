package skyport.game;

import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.message.action.ActionMessage;
import skyport.message.action.OffensiveAction;
import skyport.network.ai.AIConnection;
import skyport.network.graphics.GraphicsConnection;

public class Game implements Runnable {
    private List<AIConnection> clients;
    private int gameTimeoutSeconds;
    private int roundTimeMilliseconds;
    // TODO: set this to 'false' to be more compliant with spec
    private boolean accelerateDeadPlayers = true;
    private World world;
    private GraphicsConnection graphics;

    private final Logger logger = LoggerFactory.getLogger(Game.class);

    public Game(GraphicsConnection graphics, List<AIConnection> clients, int gameTimeoutSeconds, int roundTimeMilliseconds, World world) {
        this.graphics = graphics;
        this.world = world;
        this.clients = clients;
        Collections.shuffle(this.clients);
        this.gameTimeoutSeconds = gameTimeoutSeconds;
        this.roundTimeMilliseconds = roundTimeMilliseconds;
    }

    @Override
    public void run() {
        logger.debug("Initializing game.");

        Queue<Tile> spawnpoints = world.getSpawnpoints();
        for (AIConnection client : clients) {
            Tile spawn = spawnpoints.poll();
            if (spawn == null) {
                throw new RuntimeException("Fewer spawnpoints then clients.");
            }
            client.setSpawnpoint(spawn);
        }

        logger.info("Sending initial gamestart packet.");
        sendGamestate(0);
        // we just loop until everyone has selected a loadout.
        boolean allAreReady = true;
        while (true) {
            for (AIConnection client : clients) {
                if (!client.gotLoadout()) {
                    logger.info("Waiting for loadout from " + client.getPlayer().getName() + ".");
                    allAreReady = false;
                }
            }
            if (allAreReady) {
                logger.info("All clients are ready.");
                break;
            }

            letClientsThink();
        }
        sendDeadline();
        graphics.sendEndActions();
        graphics.waitForGraphics();
        logger.debug("All clients have sent a loadout.");
        long startTime = System.nanoTime();
        long gtsAsLong = gameTimeoutSeconds;
        int roundNumber = 1;

        boolean sixtyMarker = false;
        boolean thirtyMarker = false;
        boolean twentyMarker = false;
        boolean tenMarker = false;

        while (true) {
            logger.debug("####################### GAME STATS: #######################");
            for (AIConnection client : clients) {
                logger.info(client.getPlayer().toString());
            }
            logger.debug("###########################################################");
            int playerNum = world.verifyNumberOfPlayersOnBoard();
            if (playerNum != clients.size()) {
                logger.warn(clients.size() + " players are supposed to be" + " on the field, but found " + playerNum + ". Possible inconsistency during movement?");
            }

            long roundStartTime = System.nanoTime();
            double timeLeft = (gtsAsLong - ((roundStartTime - startTime) / 1000000000.0));
            if (timeLeft < 60 && !sixtyMarker) {
                graphics.sendMessage("60 seconds left!");
                sixtyMarker = true;
            }
            if (timeLeft < 30 && !thirtyMarker) {
                graphics.sendMessage("30 seconds left!");
                thirtyMarker = true;
            }
            if (timeLeft < 20 && !twentyMarker) {
                graphics.sendMessage("20 seconds left!");
                twentyMarker = true;
            }
            if (timeLeft < 10 && !tenMarker) {
                graphics.sendMessage("10 seconds left!");
                tenMarker = true;
            }
            if ((roundStartTime - startTime) > gtsAsLong * 1000000000) {
                logger.info("Time over!");
                int highestScore = -1000000000;
                Player winningPlayer = clients.get(0).getPlayer();
                for (AIConnection connection : clients) {
                    if (connection.getPlayer().score > highestScore) {
                        highestScore = connection.getPlayer().score;
                        winningPlayer = connection.getPlayer();
                    }
                }
                String wintext = winningPlayer.getName() + " wins!";
                for (AIConnection connection : clients) {
                    if (connection.getPlayer().score == highestScore && !winningPlayer.equals(connection.getPlayer())) {
                        wintext = "Tie!";
                    }
                }
                while (true) {
                    graphics.sendMessage("Time over!");
                    letClientsThink();
                    graphics.sendMessage(wintext);
                    letClientsThink();
                }
            }
            AIConnection currentPlayer = sendGamestate(roundNumber);
            logger.info("############### START TURN " + roundNumber + " PLAYER: '" + currentPlayer.getPlayer().getName() + "' ###############");
            if (!currentPlayer.isAlive() && accelerateDeadPlayers) {
                logger.debug("Player '" + currentPlayer.getPlayer().getName() + "' is dead and accelerateDeadPlayers flag is set, sending" + " deadline immediately...");
            } else {
                processThreePlayerActions(currentPlayer);
                givePenalityForLingeringOnSpawntile(currentPlayer);
                graphics.sendEndActions();
                graphics.waitForGraphics();
                syncWithGraphics();
                roundNumber++;
            }
            this.sendDeadline();
        }
    }

    private void givePenalityForLingeringOnSpawntile(AIConnection currentPlayer) {
        if (currentPlayer.getPlayer().position == currentPlayer.getPlayer().getSpawn()) {
            logger.warn("Player " + currentPlayer.getPlayer() + " stayed on spawn too long.");
            currentPlayer.givePenality(10);
        }
    }

    private void processThreePlayerActions(final AIConnection currentPlayer) {
        int validActions = 0;
        long timeout = this.roundTimeMilliseconds;
        long current = System.currentTimeMillis();
        for (int turn = 0; turn < 3; turn++) {
            ActionMessage action = currentPlayer.getNextMessage(timeout, TimeUnit.MILLISECONDS);

            if (action == null) {
                continue;
            }

            try {
                currentPlayer.getPlayer().setTurnsLeft(2-turn);
                if (action.performAction(currentPlayer.getPlayer())) {
                    broadcastAction(action, currentPlayer);
                    validActions++;
                    if (action instanceof OffensiveAction) {
                        return;
                    }
                }
            } catch (ProtocolException e) {
                currentPlayer.sendError(e.getMessage());
            }

            timeout = Math.max(0, timeout - (System.currentTimeMillis() - current));
            current = System.currentTimeMillis();
        }     
        timeout = Math.max(0, timeout - (System.currentTimeMillis() - current));
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        logger.info("==> Player " + currentPlayer.getPlayer() + " performed " + validActions + " valid actions.");
        if (validActions == 0) {
            currentPlayer.givePenality(10);
        }
    }

    private void broadcastAction(ActionMessage action, AIConnection playerWhoPerformedTheAction) {
        logger.debug("Action was valid, re-broadcasting (FIXME).");
        action.setFrom(playerWhoPerformedTheAction.getPlayer().getName());
        System.out.println("ACTION: " + action.toString());

        graphics.sendMessage(action);
        for (AIConnection player : clients) {
            player.sendMessage(action);
        }
    }

    private void syncWithGraphics() {
        int newRoundTime = graphics.thinktime;
        if (newRoundTime != roundTimeMilliseconds) {
            roundTimeMilliseconds = newRoundTime;
            logger.info("Delay changed to " + newRoundTime + ".");
            graphics.sendMessage("Delay changed to " + newRoundTime + ".");
        }
    }

    private void letClientsThink() {
        try {
            Thread.sleep(roundTimeMilliseconds); // 1000
        } catch (InterruptedException e) {
            logger.warn("INTERRUPTED!");
        }
    }

    private AIConnection sendGamestate(int round) {
        TileType matrix[][] = world.returnAsRowMajorMatrix();

        GameMap map = new GameMap(world.getJLength(), world.getKLength(), matrix);

        if (round != 0) {
            clients.get(0).clearAllMessages();
        }
        graphics.sendGamestate(round, map, clients);
        for (AIConnection client : clients) {
            client.sendGamestate(round, map, clients);
        }
        Collections.rotate(clients, 1);
        return clients.get(0);
    }

    private void sendDeadline() {
        graphics.sendDeadline();
        for (AIConnection client : clients) {
            client.sendDeadline();
        }
    }
}
