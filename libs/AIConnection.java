import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.*;

public class AIConnection {
    private Socket socket;
    private BufferedReader inputReader;
    private ConcurrentLinkedQueue<JSONObject> messages;
    private boolean gotHandshake = false;
    public AtomicBoolean gotLoadout = new AtomicBoolean(false);
    public String primaryWeapon = "";
    public String secondaryWeapon = "";
    public int primaryWeaponLevel = 1;
    public int secondaryWeaponLevel = 1;
    public String username;
    public Tile position = null;
    public boolean isAlive = true;
    
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
	else if(o.has("message")){
	    try {
		if(o.get("message").equals("action")){
		    messages.add(o);
		}
		else {
		    throw new ProtocolException("Unexpected message, got '" + o.get("message")
						+ "' but expected 'action'");
		}
	    }
	    catch (JSONException e){
		throw new ProtocolException("Invalid or incomplete packet: " + e.getMessage());
	    }
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
	    Util.validateUsername(o.getString("name"));
	    username = o.getString("name");
	    gotHandshake = true;
	    return true;
	}
	catch (JSONException e){
	    throw new ProtocolException("Invalid or incomplete packet: " + e.getMessage());
	}
    }
    public void sendGamestate(int turnNumber, int dimension, String mapData[][],
			      AIConnection playerlist[]){
	JSONObject root = new JSONObject();
	try {
	    root.put("message", "gamestate");
	    root.put("turn", turnNumber);
	    JSONArray players = new JSONArray();
	    
	    for(AIConnection ai: playerlist){
		JSONObject playerObject = new JSONObject();
		playerObject.put("name", ai.username);
		if(turnNumber != 0){
		    playerObject.put("health", 100);
		    playerObject.put("score", 0);
		    playerObject.put("position", ai.position.coords.getCompactString());
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
	}
	catch (JSONException e){}
	try {
	    sendMessage(root);
	}
	catch (IOException e) {
	    System.out.println("Error writing to '" + username + "': " + e.getMessage());
	}
    }
    public void sendDeadline(){
	JSONObject o = new JSONObject();
	try {
	    o.put("message", "endturn");
	    sendMessage(o);
	} catch (JSONException e){}
	catch (IOException e){
	    System.out.println("Error writing to '" + username + "': " + e.getMessage());
	}
    }
    public void sendMessage(JSONObject o) throws IOException{
	socket.getOutputStream().write((o.toString() + "\n").getBytes());
    }
    public JSONObject getNextMessage(){
	return messages.poll();
    }
    public synchronized void setSpawnpoint(Tile spawnpoint){
	System.out.println("[AICONHND] Player '" + username
			   + "' spawns at " + spawnpoint.coords.getString());
	position = spawnpoint;
	position.playerOnTile = this;
    }
    public void clearAllMessages(){
	if(messages.size() > 0){
	    System.out.println("Message inbox of " + username + " contained " + messages.size() + " extra messages, discarding...");
	}
	messages.clear();
    }
    public synchronized boolean doMove(JSONObject o){
	// TODO verify that this is all exactly right
	try {
	    String direction = o.getString("direction");
	    if(direction.equals("up")){
		if(position.up != null && position.up.isAccessible()){
		    if(position.up.playerOnTile != null)
			throw new ProtocolException("Player " + position.up.playerOnTile.username
						    + " is already on this tile.");
		    position.playerOnTile = null;
		    position = position.up;
		    position.playerOnTile = this;
		    return true;
		}
		else {
		    throw Util.throwInaccessibleTileException("up", position.up);
		}
	    }
	    else if(direction.equals("down")){
		if(position.down != null && position.down.isAccessible()){
		    if(position.down.playerOnTile != null)
			throw new ProtocolException("Player " + position.down.playerOnTile.username
						    + " is already on this tile.");
		    position.playerOnTile = null;
		    position = position.down;
		    position.playerOnTile = this;
		    return true;
		}
		else {
		    throw Util.throwInaccessibleTileException("down", position.down);
		}
	    }
	    else if(direction.equals("left-down")){
		if(position.leftDown != null && position.leftDown.isAccessible()){
		    if(position.leftDown.playerOnTile != null)
			throw new ProtocolException("Player " + position.leftDown.playerOnTile.username
						    + " is already on this tile.");
		    position.playerOnTile = null;
		    position = position.leftDown;
		    position.playerOnTile = this;
		    return true;
		}
		else {
		    throw Util.throwInaccessibleTileException("left-down", position.leftDown);
		}
	    }
	    else if(direction.equals("left-up")){
		if(position.leftUp != null && position.leftUp.isAccessible()){
		    if(position.leftUp.playerOnTile != null)
			throw new ProtocolException("Player " + position.leftUp.playerOnTile.username
						    + " is already on this tile.");
		    position.playerOnTile = null;
		    position = position.leftUp;
		    position.playerOnTile = this;
		    return true;
		}
		else {
		    throw Util.throwInaccessibleTileException("left-up", position.leftUp);
		}
	    }
	    else if(direction.equals("right-down")) {
		if(position.rightDown != null && position.rightDown.isAccessible()){
		    if(position.rightDown.playerOnTile != null)
			throw new ProtocolException("Player " + position.rightDown.playerOnTile.username
						    + " is already on this tile.");
		    position.playerOnTile = null;
		    position = position.rightDown;
		    position.playerOnTile = this;
		    return true;
		}
		else {
		    throw Util.throwInaccessibleTileException("right-down", position.rightDown);
		}
	    }
	    else if(direction.equals("right-up")){
		if(position.rightUp != null && position.rightUp.isAccessible()){
		    if(position.rightUp.playerOnTile != null)
			throw new ProtocolException("Player " + position.rightUp.playerOnTile.username
						    + " is already on this tile.");
		    position.playerOnTile = null;
		    position = position.rightUp;
		    position.playerOnTile = this;
		    return true;
		}
		else {
		    throw Util.throwInaccessibleTileException("right-up", position.rightUp);
		}
	    }
	    else {
		throw new ProtocolException("Invalid direction: '" + direction + "'");
	    }
	}
	catch (JSONException e){
	    try {
		JSONObject errorMessage
		    = new JSONObject().put("error", "Invalid move packet: " + e.getMessage());
		    sendMessage(errorMessage);
	    } catch (JSONException f){}
	    catch (IOException g){}
	    return false;
	}
	catch (ProtocolException e){
	    try {
		JSONObject errorMessage
		    = new JSONObject().put("error", "Invalid move packet: " + e.getMessage());
		sendMessage(errorMessage);
	    } catch (JSONException f){}
	    catch (IOException g){}
	    return false;
	}
    }
}
