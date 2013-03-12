import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Acceptor implements Runnable {
    private ServerSocket acceptorSocket;
    private ConcurrentLinkedQueue<AIConnection> globalClientList;
    private int backlog;
    private int clientsAccepted = 0;
    private boolean isGraphicsManager = false;
    public GraphicsContainer graphics;
    public Acceptor(int port, ConcurrentLinkedQueue<AIConnection> globalClients, int backlogArg, boolean isGraphicsManagerArg, GraphicsContainer graphicsContainer){
	try {
	    acceptorSocket = new ServerSocket(port);
	    acceptorSocket.setReuseAddress(true);
	    graphics = graphicsContainer;
	    backlog = backlogArg;
	    globalClientList = globalClients;
	    isGraphicsManager = isGraphicsManagerArg;
	    if(isGraphicsManager){
		Debug.info("listening on port " + acceptorSocket.toString() + " for the GUI");
	    }
	    else {
		Debug.info("listening on port " + acceptorSocket.toString() + " for players");
	    }
	}
	catch(IOException e){
	    Debug.error("Error binding to port: " + e);
	}
    }
    public void run(){
	Debug.debug("Starting to accept incoming connections");
	while(clientsAccepted < backlog){
	    try {
		Socket clientSocket = acceptorSocket.accept();
		Debug.debug("Connect from " + clientSocket);
		if(!isGraphicsManager){
		    spawnReadHandlerThread(clientSocket);
		}
		else {
		    spawnGraphicsHandlerThread(clientSocket);
		}
		clientsAccepted++;
	    }
	    catch (IOException e) {
		Debug.warn("Error accepting connection: " + e);
	    }
	}
	Debug.debug("Accepted " + backlog + " clients, exiting");
	try {
	    acceptorSocket.close();
	}
	catch (IOException e){}
    };

    public GraphicsConnection spawnGraphicsHandlerThread(Socket clientSocket){
	Debug.debug("Spawning graphics handler!");
	GraphicsConnection conn = new GraphicsConnection(clientSocket, graphics);
	GraphicsClientHandler handler = new GraphicsClientHandler(conn);
	Thread thread = new Thread(handler);
	thread.start();
	return conn;
    }
    
    public void spawnReadHandlerThread(Socket clientSocket){
	AIConnection aiConnectionObject = new AIConnection(clientSocket);
	globalClientList.add(aiConnectionObject);
	AIClientHandler handler = new AIClientHandler(aiConnectionObject, globalClientList);
	Thread thread = new Thread(handler);
	thread.start();
    }
}
