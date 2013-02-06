import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.json.*;

public class AIConnection {
    private Socket socket;
    private BufferedReader inputReader;
    private ConcurrentLinkedQueue<String> messages;
    private StatefulProtocolDecoder decoder;
    public AIConnection(Socket clientSocket){
	messages = new ConcurrentLinkedQueue<String>();
	socket = clientSocket;
	decoder = new StatefulProtocolDecoder();
	try {
	    inputReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}
	catch (IOException e){
	    System.out.println("[AICONHND] error creating connection handler: " + e);
	}
    }
    public String getIp(){
	return socket.getInetAddress() + ":" + Integer.toString(socket.getPort());
    }
    public synchronized void write(String string){ // only one thread may write at the same time
	System.out.println("[AICONHND] writing to socket: " + string);
    }
    public synchronized boolean read(){ // only one thread may perform a blocking read at the same time
	String returnToClient = null;
	try {
	    String line = inputReader.readLine();
	    if(line == null){
		return false;
	    }
	    try {
		returnToClient = decoder.parseLine(line);
		if(returnToClient != null){
		    sendMessage(returnToClient);
		}
	    }
	    catch (ProtocolException e){
		JSONStringer stringer = new JSONStringer();
		String jsonErrorMessage = null;
		try {
		    jsonErrorMessage = stringer.object().key("error").value(e.getMessage()).endObject().toString();
		}
		catch (JSONException f){}
		System.out.println("protocol exception: " + e);
		sendMessage(jsonErrorMessage);
		// socket.getOutputStream().write((jsonErrorMessage + "\n").getBytes());
	    }
	    messages.add(line);
	    return true;
	}
	catch (IOException e){
	    System.out.println("[AICONHND] Error reading from socket: " + e);
	    return false;
	}
    }
    public void sendMessage(String string) throws IOException{
	socket.getOutputStream().write((string + "\n").getBytes());
    }
    public String getNextMessage(){
	return messages.poll();
    }
}
