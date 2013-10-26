package skyport.network.graphics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import skyport.debug.Debug;
import skyport.exception.ProtocolException;
import skyport.game.Coordinate;
import skyport.network.ai.AIConnection;

public class GraphicsConnection {
    public static GraphicsConnection debugConnection;
    private Socket socket;
    private BufferedReader inputReader;
    private ConcurrentLinkedQueue<JSONObject> messages;
    private boolean gotHandshake = false;
    String password = "supersecretpassword";
    public boolean isAlive = true;
    public GraphicsContainer container = null;
    public boolean isDoneProcessing = true;
    public boolean alternativeLaserStyle = false;
    public Coordinate startHack = null;
    public Coordinate stopHack = null;
    public int thinktime;

    public GraphicsConnection(Socket clientSocket, GraphicsContainer containerArg) {
        messages = new ConcurrentLinkedQueue<JSONObject>();
        container = containerArg;
        socket = clientSocket;
        try {
            inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            Debug.error("error creating connection handler: " + e);
        }
        debugConnection = this;
    }

    public String readLine() throws IOException {
        String line = inputReader.readLine();
        return line;
    }

    public String getIp() {
        return socket.getInetAddress() + ":" + Integer.toString(socket.getPort());
    }

    public synchronized void input(JSONObject o) throws ProtocolException, IOException {
        if (!gotHandshake) {
            if (parseHandshake(o)) {
                try {
                    JSONObject successMessage = new JSONObject().put("message", "connect").put("status", true);
                    sendMessage(successMessage);
                } catch (JSONException e) {
                }
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

    public synchronized void sendGamestate(int turnNumber, int dimension, String mapData[][], AIConnection playerlist[]) {
        JSONObject root = new JSONObject();
        try {
            root.put("message", "gamestate");
            root.put("turn", turnNumber);
            JSONArray players = new JSONArray();
            for (AIConnection ai : playerlist) {
                JSONObject playerObject = new JSONObject();
                playerObject.put("name", ai.username);
                if (turnNumber != 0) {
                    playerObject.put("health", ai.health);
                    playerObject.put("score", ai.score);
                    playerObject.put("position", ai.position.coords.getCompactString());
                    playerObject.put("alive", ai.isAlive);
                    JSONObject primaryWeaponObject = new JSONObject();
                    primaryWeaponObject.put("name", ai.primaryWeapon);
                    primaryWeaponObject.put("level", ai.primaryWeaponLevel);
                    playerObject.put("primary-weapon", primaryWeaponObject);

                    JSONObject secondaryWeaponObject = new JSONObject();
                    secondaryWeaponObject.put("name", ai.secondaryWeapon);
                    secondaryWeaponObject.put("level", ai.secondaryWeaponLevel);
                    playerObject.put("secondary-weapon", secondaryWeaponObject);
                }

                players.put(playerObject);
            }
            root.put("players", players);

            JSONObject map = new JSONObject();
            map.put("j-length", dimension);
            map.put("k-length", dimension);
            map.put("data", new JSONArray(mapData));
            root.put("map", map);
        } catch (JSONException e) {
        }
        try {
            sendMessage(root);
            isDoneProcessing = false;
        } catch (IOException e) {
            Debug.error("Error writing to graphics: " + e.getMessage());
        }
    }

    public void sendDeadline() {
        JSONObject o = new JSONObject();
        try {
            o.put("message", "endturn");
            sendMessage(o);
        } catch (JSONException e) {
        } catch (IOException e) {
            Debug.error("Error writing to graphics: " + e.getMessage());
        }
    }

    public void sendEndActions() {
        JSONObject o = new JSONObject();
        try {
            o.put("message", "endactions");
            sendMessage(o);
        } catch (JSONException e) {
        } catch (IOException e) {
            Debug.error("Error writing to graphics: " + e.getMessage());
        }
    }

    public void sendMessage(JSONObject o) throws IOException {
        try {
            if (o.getString("message").equals("action") && o.getString("type").equals("laser")) {
                o.put("start", startHack.getCompactString());
                o.put("stop", stopHack.getCompactString());
            }
        } catch (JSONException e) {
        }

        socket.getOutputStream().write((o.toString() + "\n").getBytes());
    }

    public JSONObject getNextMessage() {
        return messages.poll();
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
        JSONObject o = new JSONObject();
        try {
            o.put("message", "title");
            o.put("text", title);
            sendMessage(o);
        } catch (JSONException e) {
        } catch (IOException e) {
        }
    }

    public void sendSubtitle(String subtitle) {
        JSONObject o = new JSONObject();
        try {
            o.put("message", "subtitle");
            o.put("text", subtitle);
            sendMessage(o);
        } catch (JSONException e) {
        } catch (IOException e) {
        }
    }

    public void sendHighlight(String position, int r, int g, int b) {
        JSONObject o = new JSONObject();
        try {
            o.put("message", "highlight");
            o.put("coordinate", position);
            o.put("color", new JSONArray().put(r).put(g).put(b));
            sendMessage(o);
        } catch (JSONException e) {
        } catch (IOException e) {
        }
    }

    public void sendMessage(String message) {
        JSONObject o = new JSONObject();
        try {
            o.put("message", "subtitle");
            o.put("text", message);
            sendMessage(o);
        } catch (JSONException e) {
        } catch (IOException e) {
        }
    }
}
