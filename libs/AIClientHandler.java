import java.util.concurrent.ConcurrentLinkedQueue;
import org.json.*;
import java.io.IOException;

public class AIClientHandler implements Runnable {
    AIConnection connection;
    ConcurrentLinkedQueue<AIConnection> globalClientList;
    public AIClientHandler(AIConnection aiConnection, ConcurrentLinkedQueue<AIConnection> lq) {
	connection = aiConnection;
	globalClientList = lq;
	Debug.info("Got new AI client. " + lq.size() + " clients are now active.");
    }
    @Override
    public void run(){
	while(true){
	    try {
		JSONObject o = read();
		if(o == null){
		    throw new IOException("Client disconnected");
		}
		connection.input(o);
	    }
	    catch (IOException e){
		//globalClientList.remove(connection);
		Debug.warn("Disconnect from " + connection.getIp() + ". "
				   + globalClientList.size() + " clients active.");
		connection.isAlive = false;
		return;
	    }
	    catch (ProtocolException e){
		try {
		    JSONObject errorMessage = new JSONObject().put("error", e.getMessage());
		    connection.sendMessage(errorMessage);
		}
		catch (JSONException f){}
		catch (IOException g){
		}
	    }
	}
    }
    
    public synchronized JSONObject read() throws IOException, ProtocolException {
	String line = connection.readLine();
	JSONObject obj = null;
	if(line == null){
	    return null;
	}
	try {
	    obj = new JSONObject(line);
	    return obj;
	}
	catch (JSONException e){
	    throw new ProtocolException("Invalid packet received: " + e.getMessage());
	}
    }
}
