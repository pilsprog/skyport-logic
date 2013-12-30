package skyport.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.adapter.ActionMessageDeserializer;
import skyport.adapter.PointAdapter;
import skyport.exception.ProtocolException;
import skyport.game.GameMap;
import skyport.game.Player;
import skyport.game.Point;
import skyport.message.EndTurnMessage;
import skyport.message.ErrorMessage;
import skyport.message.GameStateMessage;
import skyport.message.Message;
import skyport.message.action.ActionMessage;
import skyport.network.ai.AIConnection;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Connection implements Runnable {
    protected Socket socket;
    protected String identifier;
    protected BufferedReader input;
    protected BufferedWriter output;
    protected Queue<ActionMessage> messages = new LinkedList<>();
    protected boolean isAlive = true;
    protected boolean gotHandshake = false;

    private final Logger logger = LoggerFactory.getLogger(Connection.class);

    protected Gson gson = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
        .registerTypeAdapter(Point.class, new PointAdapter())
        .registerTypeAdapter(ActionMessage.class, new ActionMessageDeserializer())
        .create();

    public Connection(Socket socket) {
        this.socket = socket;
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            logger.error("error creating connection handler: " + e);
        }
    }

    @Override
    public void run() {
        for (;;) {
            try {
                String json = this.readLine();
                if (json == null) {
                    throw new IOException("Client disconnected.");
                }
                this.input(json);
            } catch (IOException e) {
                logger.warn("Disconnect from " + this.getClass().getSimpleName() + ":" + this.getIP() + ".");
                this.close();
                return;
            } catch (ProtocolException e) {
                this.sendError(e.getMessage());
            }
        }
    }

    protected abstract void input(String json) throws IOException, ProtocolException;

    public boolean isAlive() {
        return isAlive;
    }

    public void close() {
        isAlive = false;
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
            logger.error("Error writing to '" + identifier + "': " + e.getMessage());
        }
    }

    public void sendMessage(Message message) {
        String json = gson.toJson(message);
        this.sendMessage(json);

    }

    public void sendDeadline() {
        Message endTurn = new EndTurnMessage();
        this.sendMessage(endTurn);
    }

    public ActionMessage getNextMessage() {
        return messages.poll();
    }

    public void sendGamestate(int turn, GameMap map, List<AIConnection> playerlist) {
        List<Player> players = new ArrayList<>();
        for (AIConnection ai : playerlist) {
            players.add(ai.getPlayer());
        }

        Message message = new GameStateMessage(turn, map, players);
        sendMessage(message);
    }

    public void sendError(String errorString) {
        Message error = new ErrorMessage(errorString);
        sendMessage(error);
    }
}
