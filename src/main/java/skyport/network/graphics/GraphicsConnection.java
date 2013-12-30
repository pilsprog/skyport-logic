package skyport.network.graphics;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.GameMap;
import skyport.message.EndActionsMessage;
import skyport.message.GraphicsHandshakeMessage;
import skyport.message.HighlightMessage;
import skyport.message.Message;
import skyport.message.StatusMessage;
import skyport.message.SubtitleMessage;
import skyport.message.TitleMessage;
import skyport.network.Connection;
import skyport.network.ai.AIConnection;

public class GraphicsConnection extends Connection {
    private String password = "supersecretpassword";

    public boolean isDoneProcessing = true;
    public boolean alternativeLaserStyle = false;
    public int thinktime;

    private final Logger logger = LoggerFactory.getLogger(GraphicsConnection.class);

    public GraphicsConnection(Socket socket) {
        super(socket);
        this.identifier = "graphics";
    }

    @Override
    protected void input(String json) throws ProtocolException, IOException {
        if (!gotHandshake) {
            if (parseHandshake(json)) {
                Message success = new StatusMessage(true);
                this.sendMessage(success);
            }
            return;
        } else {
            Message message = gson.fromJson(json, Message.class);
            String text = message.getMessage();
            if (text != null) {
                if (text.equals("ready")) {
                    logger.debug("DONE PROCESSING.");
                    isDoneProcessing = true;
                } else if (text.equals("faster")) {
                    if (thinktime >= 200) {
                        thinktime -= 100;
                        logger.info("New timeout: " + thinktime + " milliseconds.");
                    } else {
                        this.sendMessage("Can't go faster.");
                        logger.info("Can't go faster.");
                    }
                } else if (text.equals("slower")) {
                    thinktime += 100;
                    logger.info("New timeout: " + thinktime + " milliseconds.");
                } else {
                    throw new ProtocolException("Unexpected message, got '" + text + "' but expected 'action'.");
                }
            } else {
                logger.warn("Unexpected packet: " + text);
                throw new ProtocolException("Unexpected packet: '" + text + "'.");
            }
        }
    }

    private boolean parseHandshake(String json) throws ProtocolException {
        GraphicsHandshakeMessage handshake = gson.fromJson(json, GraphicsHandshakeMessage.class);
        String message = handshake.getMessage();
        if (!message.equals("connect")) {
            throw new ProtocolException("Expected 'connect' handshake, but got '" + message + "' key.");
        }

        int revision = handshake.getRevision();
        if (revision != 1) {
            throw new ProtocolException("Wrong protocol revision: supporting 1, but got " + revision + ".");
        }
        if (!handshake.validatePassword(this.password)) {
            logger.warn("GUI sent wrong password.");
            throw new ProtocolException("Wrong password!");
        }
        logger.debug("Correct password.");
        gotHandshake = true;

        String laserStyle = handshake.getLaserStyle();
        if (laserStyle.equals("start-stop")) {
            alternativeLaserStyle = true;
        }

        return true;
    }

    @Override
    public void sendGamestate(int turn, GameMap map, List<AIConnection> playerlist) {
        super.sendGamestate(turn, map, playerlist);
        isDoneProcessing = false;
    }

    public void sendEndActions() {
        Message endActions = new EndActionsMessage();
        this.sendMessage(endActions);
    }

    public void waitForGraphics() {
        logger.debug("Waiting for graphics...");
        while (true) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
            if (isDoneProcessing) {
                break;
            }
        }
    }

    public void sendTitle(String title) {
        Message message = new TitleMessage(title);
        this.sendMessage(message);
    }

    public void sendSubtitle(String subtitle) {
        Message message = new SubtitleMessage(subtitle);
        this.sendMessage(message);
    }

    public void sendHighlight(String position, int r, int g, int b) {
        Message message = new HighlightMessage(position, r, g, b);
        this.sendMessage(message);
    }

    public void sendMessage(String message) {
        Message subtitle = new SubtitleMessage(message);
        this.sendMessage(subtitle);
    }

    public void setThinkTimeout(int roundTimeMilliseconds) {
        this.thinktime = roundTimeMilliseconds;

    }
}
