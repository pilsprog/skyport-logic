import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.*;
import java.util.regex.Pattern;

public class AIConnection {
    private Socket socket;
    private BufferedReader inputReader;
    private ConcurrentLinkedQueue<JSONObject> messages;
    private boolean gotHandshake = false;
    public AtomicBoolean gotLoadout = new AtomicBoolean(false);
    public String primaryWeapon = null;
    public String secondaryWeapon = null;
    private String username;
    
    public AIConnection(Socket clientSocket){
	messages = new ConcurrentLinkedQueue<JSONObject>();
	socket = clientSocket;
	try {
	    inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	catch (IOException e){
	    System.out.println("[AICONHND] error creating connection handler: " + e);
	}
    }
    public String readLine() throws IOException {
	return inputReader.readLine();
    }
    public String getIp(){
	return socket.getInetAddress() + ":" + Integer.toString(socket.getPort());
    }
    public synchronized void write(String string){ // only one thread may write at the same time
	System.out.println("[AICONHND] writing to socket: " + string);
    }
    public synchronized void input(JSONObject o) throws ProtocolException, IOException {
	System.out.println("[AICONHND] Got input");
	if(!gotHandshake){
	    if(parseHandshake(o)){
		try {
		    JSONObject successMessage = new JSONObject()
			.put("message", "connect").put("status", true);
		    sendMessage(successMessage);
		}
		catch (JSONException e) {}
	    }
	    return;
	}
	else if(!gotLoadout.get()){
	    parseLoadout(o);
	}
	else {
	    try {
		throw new ProtocolException("Unexpected packet: '" + o.get("message") + "'");
	    }
	    catch (JSONException e) {
		throw new ProtocolException("Invalid or incomplete packet");
	    }
	}
    }

    private void parseLoadout(JSONObject o) throws ProtocolException {
	try {
	    if(!(o.get("message").equals("loadout"))){
		throw new ProtocolException("Expected 'loadout', but got '" + o.get("message") + "' key");
	    }
	    if(!Util.validateWeapon(o.getString("primary-weapon"))){
		throw new ProtocolException("Invalid primary weapon: '"
					    + o.getString("primary-weapon") + "'");
	    }
	    if(!Util.validateWeapon(o.getString("secondary-weapon"))){
		throw new ProtocolException("Invalid secondary weapon: '"
					    + o.getString("secondary-weapon") + "'");
	    }
	    if(o.getString("primary-weapon").equals(o.getString("secondary-weapon"))){
		throw new ProtocolException("Invalid loadout: Can't have the same weapon twice.");
	    }
	    primaryWeapon = o.getString("primary-weapon");
	    secondaryWeapon = o.getString("secondary-weapon");
	    System.out.println(username + " selected loadout: " + primaryWeapon + " and "
			       + secondaryWeapon + ".");
	    gotLoadout.set(true);
	}
	catch (JSONException e){
	    throw new ProtocolException("Invalid or incomplete packet: " + e.getMessage());	    
	}
    }

    private boolean parseHandshake(JSONObject o) throws ProtocolException {
	try {
	    if(!(o.get("message").equals("connect"))){
		throw new ProtocolException("Expected 'connect' handshake, but got '"
					    + o.get("message") + "' key");
	    }
	    if(!(o.getInt("revision") == 1)){
		throw new ProtocolException("Wrong protocol revision: supporting 1, but got " +
					    o.getInt("revision"));
	    }
	    validateUsername(o.getString("name"));
	    username = o.getString("name");
	    gotHandshake = true;
	    return true;
	}
	catch (JSONException e){
	    throw new ProtocolException("Invalid or incomplete packet: " + e.getMessage());
	}
    }

    private void validateUsername(String username) throws ProtocolException {
	if(username.length() < 3){
	    throw new ProtocolException("Username too short: needs to be 3 characters or longer.");
	}
	if(username.length() > 16){
	    throw new ProtocolException("Username too long: needs to be 16 characters or less.");
	}
	if(!Pattern.matches("[a-zA-Z0-9-_+]+", username)){
	    throw new ProtocolException("Username contains invalid characters. May only contain "
					+ "a-z, A-Z, 0-9, -, _, +.");
	}
    }
    
    
    public void sendMessage(JSONObject o) throws IOException{
	socket.getOutputStream().write((o.toString() + "\n").getBytes());
    }
    public JSONObject getNextMessage(){
	return messages.poll();
    }
}
