package skyport.network.graphics;

import java.io.IOException;
import java.net.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import skyport.debug.Debug;
import skyport.exception.ProtocolException;
import skyport.game.Coordinate;
import skyport.game.GameMap;
import skyport.game.action.LaserAction;
import skyport.message.EndActionsMessage;
import skyport.message.HighlightMessage;
import skyport.message.Message;
import skyport.message.StatusMessage;
import skyport.message.SubtitleMessage;
import skyport.message.TitleMessage;
import skyport.network.Connection;
import skyport.network.ai.AIConnection;

public class GraphicsConnection extends Connection {
    public static GraphicsConnection debugConnection;
    
    String password = "supersecretpassword";
    public GraphicsContainer container = null;
    public boolean isDoneProcessing = true;
    public boolean alternativeLaserStyle = false;
    public Coordinate startHack = null;
    public Coordinate stopHack = null;
    public int thinktime;

    public GraphicsConnection(Socket socket, GraphicsContainer containerArg) {
        super(socket);
        container = containerArg;
        debugConnection = this;
        this.identifier = "graphics";
    }

    public synchronized void input(JSONObject o) throws ProtocolException, IOException {
        if (!gotHandshake) {
            if (parseHandshake(o)) {
                Message success = new StatusMessage(true);
                this.sendMessage(success);
            }
            return;
        } else if (o.has("message")) {
            try {
                if (o.get("message").equals("ready")) {
                    Debug.debug("DONE PROCESSING");
                    isDoneProcessing = true;
                } else if (o.get("message").equals("faster")) {
                    if (thinktime >= 200) {
                        thinktime -= 100;
                        Debug.info("New timeout: " + thinktime + " milliseconds");
                    } else {
                        Debug.guiMessage("Can't go faster");
                        Debug.info("Can't go faster");
                    }

                } else if (o.get("message").equals("slower")) {
                    thinktime += 100;
                    Debug.info("New timeout: " + thinktime + " milliseconds");
                } else {
                    throw new ProtocolException("Unexpected message, got '" + o.get("message") + "' but expected 'action'");
                }
            } catch (JSONException e) {
                Debug.warn("Invalid or incomplete packet: " + e.getMessage());
                throw new ProtocolException("Invalid or incomplete packet: " + e.getMessage());
            }
        } else {
            try {
                Debug.warn("Unexpected packet: " + o.get("message"));
                throw new ProtocolException("Unexpected packet: '" + o.get("message") + "'");
            } catch (JSONException e) {
                throw new ProtocolException("Invalid or incomplete packet");
            }
        }
    }

    private boolean parseHandshake(JSONObject o) throws ProtocolException {
        try {
            if (!(o.get("message").equals("connect"))) {
                throw new ProtocolException("Expected 'connect' handshake, but got '" + o.get("message") + "' key");
            }
            if (!(o.getInt("revision") == 1)) {
                throw new ProtocolException("Wrong protocol revision: supporting 1, but got " + o.getInt("revision"));
            }
            if (!o.getString("password").equals(password)) {
                Debug.warn("GUI sent wrong password");
                throw new ProtocolException("Wrong password!");
            }
            Debug.debug("Correct password");
            gotHandshake = true;
            container.set(this);
            try {
                if (!o.getString("laserstyle").equals("start-stop")) {
                    alternativeLaserStyle = true;
                }
            } catch (JSONException e) {
            }
            return true;
        } catch (JSONException e) {
            throw new ProtocolException("Invalid or incomplete packet: " + e.getMessage());
        }
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
    public void sendMessage(JSONObject o) {
        try {
            if (o.getString("message").equals("action") && o.getString("type").equals("laser")) {
                o.put("start", startHack.getCompactString());
                o.put("stop", stopHack.getCompactString());
            }
        } catch (JSONException e) {
        }

        super.sendMessage(o);
    }
    
    @Override
    public void sendMessage(Message message) {
        if(message instanceof LaserAction) {
            LaserAction action = (LaserAction)message;
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

    public void setStartStopHack(Coordinate startVector, Coordinate stopVector) {
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
