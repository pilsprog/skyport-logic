package skyport;

import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Comparator.comparing;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.Player;
import skyport.game.Vector2d;
import skyport.game.World;
import skyport.game.WorldParser;
import skyport.message.EndTurnMessage;
import skyport.message.ErrorMessage;
import skyport.message.GameFinishedMessage;
import skyport.message.GameStateMessage;
import skyport.message.Message;
import skyport.message.action.ActionMessage;
import skyport.network.Connection;
import skyport.network.GraphicsServer;

public class Skyport {
    final static Logger logger = LoggerFactory.getLogger(Skyport.class);

    final static Options options = new Options()
        .addOption("port", "p", false, "The port to run the server on.")
        .addOption("rounds", "r", false, "How many rounds the bots should play.")
        .addOption("timeout", "t", false, "How long a round should last in milliseconds.")
        .addOption("map", "f", true, "The file that contains the map.");
    
    public static void runGame(String filename, int port, int rounds, int time) throws IOException, InterruptedException, ExecutionException {
        final World world;
        int users = 0;
        try {
            WorldParser wp = new WorldParser();
            world = wp.parse(filename);
            users = world.getNumberOfSpawnpoints();
        } catch (FileNotFoundException e) {
            logger.error("Map file not found: '" + filename + "'.");
            return;
        }
        
        logger.info("Starting graphics server.");
        GraphicsServer graphics = new GraphicsServer(port+10);
        graphics.start();
        
        logger.info("Waiting for clients to connect.");
        ServerSocket socket = new ServerSocket(port);
        List<Player> players = new ArrayList<>();
        for(int i = 0; i < users; i++) {
            Socket client = socket.accept();
            Connection conn = new Connection(client);
            new Thread(conn).start();
            players.add(new Player(conn));
        }
        
        logger.info("All clients connected.");
        
        logger.info("Shuffling players.");
        
        
        logger.info("Shaking hands with players.");
        players.stream()
                .parallel()
                .map(Player::handshake)
                .forEach(finish);
        logger.info("All hands shook.");
        
        logger.info("Setting spawnpoints.");
        Collections.shuffle(players);        
        Queue<Vector2d> spawnpoints = world.getSpawnpoints();
        for(Player player : players) {
            player.setSpawn(spawnpoints.poll());
        }
        
        logger.info("Sending initial game state.");
        logger.info("Waiting for loadout.");
        Message state0 = new GameStateMessage(0, world, players);
        players.stream()
            .peek(p -> p.send(state0))
            .map(Player::loadout)
            .forEach(finish);
        graphics.sendToAll(state0);
        logger.info("Recieved loadout from all.");
  
        logger.info("Starting game.");
        for (int round = 1; round <= rounds; round++) {
            for (Player player : players) {
                logger.info("Starting player '" + player.getName() + "' turn.");
                logger.info("Sending first round to all.");
                Message state = new GameStateMessage(round, world, players);
                players.stream().forEach(p -> p.send(state));
                graphics.sendToAll(state);

                logger.info("Clearing all messages from " + player.getName());
                player.clear();
                if (player.isDead()) {
                    logger.info(player.getName() + " is dead. Respawning...");
                    player.respawn();
                } else {
                    List<ActionMessage> messages = new ArrayList<>();
                    Instant stop = Instant.now().plus(time, MILLIS);
                    for (int a = 0; a < 3; a++) {
                        logger.info("Waiting for action " + a + ".");
                        long timeout = Math.max(0, MILLIS.between(Instant.now(), stop));
                        ActionMessage message = player.next(timeout, MILLISECONDS);
                        if (message == null) {
                            logger.debug("==> Player '" + player.getName() + "' timed out in round " + round + ".");
                            break;
                        }

                        try {
                            logger.info("Performing action.");
                            message.performAction(player, world);
                            message.setFrom(player.getName());
                            messages.add(message);
                            logger.info("Successful message: " + message.toString());
                            players.stream().forEach(p -> p.send(message));
                        } catch (ProtocolException e) {
                            logger.debug(e.getMessage());
                            player.send(new ErrorMessage(e));
                        }
                    }
                    logger.info("Waiting to complete round.");
                    Thread.sleep(Math.max(0, MILLIS.between(Instant.now(), stop)));
                    player.send(new EndTurnMessage());

                    logger.info("Sending actions to graphics.");
                    for (Message m : messages) {
                        graphics.sendToAll(m);
                    }
                }
            }
           
           Collections.rotate(players, 1);
        }
        
        String winner = players.stream()
                .max(comparing(Player::score))
                .map(Player::getName)
                .orElse("TIE!");
        
        logger.info("Winner: " + winner);
        Message message = new GameFinishedMessage(winner);
        players.stream()
            .forEach(p -> p.send(message));
        graphics.sendToAll(message); 
        socket.close();
    }

    public static void main(String args[]) throws ParseException, IOException, InterruptedException, ExecutionException {
        PropertyConfigurator.configure("log4j.properties");

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse(options, args);

        int port = Integer.parseInt(cmd.getOptionValue("port", "54321"));
        int rounds = Integer.parseInt(cmd.getOptionValue("round", "50"));
        int timeout = 1000 * Integer.parseInt(cmd.getOptionValue("timeout", "5"));
        String filename = cmd.getOptionValue("map");

        Skyport.runGame(filename, port, rounds, timeout);
    }
    
    static Consumer<Future<Void>> finish = f -> {
        try {
            f.get();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    };
}
