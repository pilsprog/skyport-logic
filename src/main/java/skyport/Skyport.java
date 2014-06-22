package skyport;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.game.Game;
import skyport.game.World;
import skyport.game.WorldParser;
import skyport.network.ai.AIAcceptor;
import skyport.network.ai.AIConnection;
import skyport.network.graphics.GraphicsAcceptor;
import skyport.network.graphics.GraphicsConnection;

public class Skyport {
    final static Logger logger = LoggerFactory.getLogger(Skyport.class);

    final static Options options = new Options()
        .addOption("port", "p", false, "The port to run the server on.")
        .addOption("turn", "s", false, "How long a turn should last in seconds.")
        .addOption("game", "t", false, "How long a game should last in seconds.")
        .addOption("map", "f", true, "The file that contains the map.");

    public static void main(String args[]) throws InterruptedException, ParseException {
        PropertyConfigurator.configure("log4j.properties");

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = parser.parse(options, args);

        int port = Integer.parseInt(cmd.getOptionValue("port", "54321"));
        int gameTimeoutMillis = 1000 * 60 * Integer.parseInt(cmd.getOptionValue("turn", "600"));
        int turnTimeoutMillis = 1000 * Integer.parseInt(cmd.getOptionValue("game", "5"));
        String mapfile = cmd.getOptionValue("map");

        logger.info("Using an AI think timeout of " + turnTimeoutMillis + "ms for each turn.");

        World world = null;
        int users = 0;
        try {
            WorldParser wp = new WorldParser(mapfile);
            world = wp.parseFile();
            users = world.getNumberOfSpawnpoints();
        } catch (FileNotFoundException e) {
            logger.error("Map file not found: '" + mapfile + "'.");
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
        graphics.setThinkTimeout(turnTimeoutMillis);

        Game game = new Game(graphics, clients, gameTimeoutMillis, turnTimeoutMillis, world);
        game.run();
    }
}
