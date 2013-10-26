package skyport.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONObject;

import skyport.debug.Debug;
import skyport.game.GameMap;
import skyport.game.Player;
import skyport.message.EndTurnMessage;
import skyport.message.GameStateMessage;
import skyport.message.Message;
import skyport.network.ai.AIConnection;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Connection {
    protected Socket socket;
    protected String identifier;
    protected BufferedReader input;
    protected BufferedWriter output;
    protected ConcurrentLinkedQueue<JSONObject> messages = new ConcurrentLinkedQueue<JSONObject>();
    public boolean isAlive = true;
    protected boolean gotHandshake = false;

    private Gson gson = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
        .create();

    public Connection(Socket socket) {
        this.socket = socket;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            Debug.error("error creating connection handler: " + e);
        }
    }

    public String readLine() throws IOException {
        String line = input.readLine();
        return line;
    }

    public String getIP() {
        return socket.getInetAddress() + ":" + Integer.toString(socket.getPort());
    }

    private void sendMessage(String json) {
        try {
            output.write(json);
            output.newLine();
            output.flush();
        } catch (IOException e) {
            Debug.error("Error writing to '" + identifier + "': " + e.getMessage());
        }
    }

    public void sendMessage(JSONObject o) {
        this.sendMessage(o.toString());
    }

    public void sendMessage(Message message) {
        String json = gson.toJson(message);
        this.sendMessage(json);

    }

    public void sendDeadline() {
        Message endTurn = new EndTurnMessage();
        this.sendMessage(endTurn);
    }

    public JSONObject getNextMessage() {
        return messages.poll();
    }

    public synchronized void sendGamestate(int turn, GameMap map, AIConnection playerlist[]) {
        List<Player> players = new ArrayList<>();
        for(AIConnection ai : playerlist) {
            players.add(ai.getPlayer());
        }

        Message message = new GameStateMessage(turn, map, players);
        sendMessage(message);
    }
}
