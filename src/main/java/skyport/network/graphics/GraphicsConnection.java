package skyport.network.graphics;

import java.io.IOException;
import java.net.Socket;

import skyport.debug.Debug;
import skyport.exception.ProtocolException;
import skyport.game.GameMap;
import skyport.game.Point;
import skyport.message.EndActionsMessage;
import skyport.message.GraphicsHandshakeMessage;
import skyport.message.HighlightMessage;
import skyport.message.Message;
import skyport.message.StatusMessage;
import skyport.message.SubtitleMessage;
import skyport.message.TitleMessage;
import skyport.message.action.LaserActionMessage;
import skyport.network.Connection;
import skyport.network.ai.AIConnection;

public class GraphicsConnection extends Connection {
    public static GraphicsConnection debugConnection;
    
    String password = "supersecretpassword";
    public GraphicsContainer container = null;
    public boolean isDoneProcessing = true;
    public boolean alternativeLaserStyle = false;
    public Point startHack = null;
    public Point stopHack = null;
    public int thinktime;

    public GraphicsConnection(Socket socket, GraphicsContainer containerArg) {
        super(socket);
        container = containerArg;
        debugConnection = this;
        this.identifier = "graphics";
    }

    public synchronized void input(String json) throws ProtocolException, IOException {
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
                    Debug.debug("DONE PROCESSING");
                    isDoneProcessing = true;
                } else if (text.equals("faster")) {
                    if (thinktime >= 200) {
                        thinktime -= 100;
                        Debug.info("New timeout: " + thinktime + " milliseconds");
                    } else {
                        Debug.guiMessage("Can't go faster");
                        Debug.info("Can't go faster");
                    }
                } else if (text.equals("slower")) {
                    thinktime += 100;
                    Debug.info("New timeout: " + thinktime + " milliseconds");
                } else {
                    throw new ProtocolException("Unexpected message, got '" + text + "' but expected 'action'");
                }
            } else {
                Debug.warn("Unexpected packet: " + text);
                throw new ProtocolException("Unexpected packet: '" + text + "'");
            }
        }
    }

    private boolean parseHandshake(String json) throws ProtocolException {
        GraphicsHandshakeMessage handshake = gson.fromJson(json, GraphicsHandshakeMessage.class);
        String message = handshake.getMessage();
        if (!message.equals("connect")) {
            throw new ProtocolException("Expected 'connect' handshake, but got '" + message + "' key");
        }

        int revision = handshake.getRevision();
        if (revision != 1) {
            throw new ProtocolException("Wrong protocol revision: supporting 1, but got " + revision);
        }
        if (!handshake.validatePassword(this.password)) {
            Debug.warn("GUI sent wrong password");
            throw new ProtocolException("Wrong password!");
        }
        Debug.debug("Correct password");
        gotHandshake = true;
        container.set(this);

        String laserStyle = handshake.getLaserStyle();
        if (laserStyle.equals("start-stop")) {
            alternativeLaserStyle = true;
        }

        return true;
    }

    @Override
    public synchronized void sendGamestate(int turn, GameMap map,AIConnection playerlist[]) {
        super.sendGamestate(turn, map, playerlist);
        isDoneProcessing = false;
    }
    
    public void sendEndActions() {
        Message endActions = new EndActionsMessage();
        this.sendMessage(endActions);
    }
    
    @Override
    public void sendMessage(Message message) {
        if(message instanceof LaserActionMessage) {
            LaserActionMessage action = (LaserActionMessage)message;
            action.setInterval(startHack, stopHack);
            super.sendMessage(action);
        } else {
            super.sendMessage(message);
        }
    }

    public void waitForGraphics() {
        Debug.debug("Waiting for graphics...");
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

    public void setStartStopHack(Point startVector, Point stopVector) {
        // quick'n'dirty hack added for skyport 2D gui to change laser to
        // start-stop format
        startHack = startVector;
        stopHack = stopVector;
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
}
