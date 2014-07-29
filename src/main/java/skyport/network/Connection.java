package skyport.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.adapter.ActionMessageDeserializer;
import skyport.adapter.MessageDeserializer;
import skyport.adapter.TileSerializer;
import skyport.adapter.Vector2dAdapter;
import skyport.game.Tile;
import skyport.game.Vector2d;
import skyport.message.Message;
import skyport.message.action.ActionMessage;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Connection implements Runnable {
    private String IP;

    private Socket socket;
    private BufferedReader input;
    private BufferedWriter output;
    private BlockingQueue<Message> messages = new LinkedBlockingQueue<>();

    private final Logger logger = LoggerFactory.getLogger(Connection.class);

    protected Gson gson = new GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_DASHES)
        .registerTypeAdapter(Vector2d.class, new Vector2dAdapter())
        .registerTypeAdapter(Message.class, new MessageDeserializer())
        .registerTypeAdapter(ActionMessage.class, new ActionMessageDeserializer())
        .registerTypeAdapter(Tile.class, new TileSerializer())
        .create();

    public Connection(Socket socket) {
        try {
            this.IP = socket.getInetAddress().toString();
            this.socket = socket;
            this.input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
           this.logger.error("Error creating connection handler:", e);
        }
    }
    
    @Override
    public void run() {
        while(!socket.isClosed()) {
            try {
                String json = input.readLine();
                Message message = gson.fromJson(json, Message.class);
                messages.put(message);
            } catch (InterruptedException | IOException e) {
            
            }
        }
    }

    public void sendMessage(Message message) {
        gson.toJson(message, output);
        try {
            output.newLine();
            output.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void clear() {
        this.messages.clear();
    }

    public void close() {
        try {
            this.input.close();
            this.output.close();
        } catch (IOException e) {   
        }   
    }

    public String getIP() {
        return IP;
    }
    
    public Message next() {
        Message message = null;
        try {
            message = messages.take();
        } catch (InterruptedException e) {
            logger.error("The wait was interrupted somehow!");
        }
        return message;
    }

    public Message next(long timeout, TimeUnit time) {
        Message message = null;
        try {
            message = messages.poll(timeout, time);
        } catch (InterruptedException e) {
           logger.error("The wait was interrupted somehow!");
        }
        return message;
    }
}
