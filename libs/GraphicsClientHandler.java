import java.util.concurrent.ConcurrentLinkedQueue;
import org.json.*;
import java.io.IOException;

public class GraphicsClientHandler implements Runnable {
    GraphicsConnection connection;
    public GraphicsClientHandler(GraphicsConnection gConnection) {
	connection = gConnection;
	Debug.debug("Created new reader thread.");
    }
    @Override
    public void run(){
	while(true){
	    try {
		JSONObject o = read();
		if(o == null){
		    throw new IOException("Graphics disconnected");
		}
		connection.input(o);
	    }
	    catch (IOException e){
		Debug.error("GUI disconnected, exiting!");
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
