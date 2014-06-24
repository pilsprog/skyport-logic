package skyport.network.graphics;

import java.io.IOException;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.message.EndActionsMessage;
import skyport.message.Message;
import skyport.message.graphics.GraphicsHandshakeMessage;
import skyport.message.graphics.HighlightMessage;
import skyport.message.graphics.SubtitleMessage;
import skyport.message.graphics.TitleMessage;
import skyport.network.Connection;

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
    public void run() {
        super.run();
        this.parseActions();
    }

    @Override
    protected void input(Message message) throws ProtocolException, IOException {

        String text = message.getMessage();
        if (text == null) {
            logger.warn("Unexpected packet: " + text);
            throw new ProtocolException("Unexpected packet: '" + text + "'.");
        }

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
    }

    protected void parseHandshake(Message message) throws ProtocolException {
        if(message instanceof GraphicsHandshakeMessage) {
            GraphicsHandshakeMessage handshake = (GraphicsHandshakeMessage)message;
        
            int revision = handshake.getRevision();
            if (revision != 1) {
                throw new ProtocolException("Wrong protocol revision: supporting 1, but got " + revision + ".");
            }
            if (!handshake.validatePassword(this.password)) {
                logger.warn("GUI sent wrong password.");
                throw new ProtocolException("Wrong password!");
            }
            logger.debug("Correct password.");


            String laserStyle = handshake.getLaserStyle();
            if (laserStyle.equals("start-stop")) {
                alternativeLaserStyle = true;
            }
        } else {
            throw new ProtocolException("Expected 'connect' handshake, but got '" + message.getMessage() + "' key.");
        }
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
