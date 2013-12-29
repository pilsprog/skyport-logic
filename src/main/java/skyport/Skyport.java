package skyport;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.game.Game;
import skyport.game.World;
import skyport.game.WorldParser;
import skyport.network.AIAcceptor;
import skyport.network.GraphicsAcceptor;
import skyport.network.ai.AIConnection;
import skyport.network.graphics.GraphicsConnection;

public class Skyport {
    final static Logger logger = LoggerFactory.getLogger(Skyport.class);

    public static void main(String args[]) throws InterruptedException {
        PropertyConfigurator.configure("log4j.properties");
        int port = 54321;
        int users = 2;
        int gameTimeoutSeconds = 600;
        int aiThinkTimeout = 3000;
        String mapfile = "";
        try {
            assert (args.length == 4 || args.length == 5);
            port = Integer.parseInt(args[0]);
            users = Integer.parseInt(args[1]);
            gameTimeoutSeconds = Integer.parseInt(args[2]);
            mapfile = args[3];
            if (args.length == 5) {
                aiThinkTimeout = Integer.parseInt(args[4]);
                logger.info("Using an AI think timeout of " + aiThinkTimeout + "ms for each turn");
            } else {
                logger.info("Using default AI think timeout of " + aiThinkTimeout + "ms for each turn");
            }
        } catch (Exception e) {
            System.out.println("Usage: ./server <port> <number of users> <game time> <mapfile> [think-timeout in milliseconds]");
            System.exit(1);
        }
        World world = null;
        int spawnPoints = 0;
        try {
            WorldParser wp = new WorldParser(mapfile);
            world = wp.parseFile();
            spawnPoints = world.getNumberOfSpawnpoints();
            if (users != 0) {
                if (spawnPoints < users) {
                    logger.error("requested to wait for " + users + " AIs, but this map only supports " + spawnPoints + ".");
                    System.exit(1);
                }
                if (spawnPoints > users) {
                    logger.warn("playing with " + users + " on a map for " + spawnPoints + " users, gameplay may be unbalanced.");
                }
            } else {
                users = spawnPoints;
            }
        } catch (FileNotFoundException e) {
            logger.error("Map file not found: '" + mapfile + "'");
            System.exit(1);
        }
        logger.info("Waiting for clients to connect.");
        AIAcceptor aiAcceptor = null;
        try {
            aiAcceptor = new AIAcceptor(port, users);
        } catch (IOException e) {
            logger.error("Error binding to port: ", e);
            System.exit(1);
        }
        GraphicsAcceptor graphicsAcceptor = null;
        try {
            graphicsAcceptor = new GraphicsAcceptor(port + 10);
        } catch (IOException e) {
            logger.error("Error binding to port: ", e);
            System.exit(1);
        }
        Thread ais = new Thread(aiAcceptor);
        Thread gs = new Thread(graphicsAcceptor);
        ais.start();
        gs.start();

        try {
            ais.join();
            gs.join();
        } catch (InterruptedException e) {
            logger.warn("Acceptors were interrupted before they finished.");
        }

        List<AIConnection> clients = aiAcceptor.getConnections();
        GraphicsConnection graphics = graphicsAcceptor.getConnection();
        graphics.setThinkTimeout(aiThinkTimeout);

        Game game = new Game(graphics, clients, gameTimeoutSeconds, aiThinkTimeout, world);
        game.run();
    }
}
