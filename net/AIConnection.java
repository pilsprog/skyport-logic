import java.io.*;
import java.net.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AIConnection {
    private Socket socket;
    private BufferedReader inputReader;
    private ConcurrentLinkedQueue<String> messages;
    public AIConnection(Socket clientSocket){
	messages = new ConcurrentLinkedQueue<String>();
	socket = clientSocket;
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
	try {
	    String line = inputReader.readLine();
	    if(line == null){
		return false;
	    }
	    messages.add(line);
	    return true;
	}
	catch (IOException e){
	    System.out.println("[AICONHND] Error reading from socket: " + e);
	    return false;
	}
    }
    public String getNextMessage(){
	return messages.poll();
    }
}
