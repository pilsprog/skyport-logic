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
	    System.out.println("[ACCEPTOR] listening on port " + acceptorSocket.toString());
	}
	catch(IOException e){
	    System.out.println("[ACCEPTOR] Error binding to port: " + e);
	    System.exit(1);
	}
    }
    public void run(){
	System.out.println("[ACCEPTOR] Starting to accept incoming connections");
	while(clientsAccepted < backlog){
	    try {
		Socket clientSocket = acceptorSocket.accept();
		System.out.println("[ACCEPTOR] Connect from " + clientSocket);
		if(!isGraphicsManager){
		    spawnReadHandlerThread(clientSocket);
		}
		else {
		    spawnGraphicsHandlerThread(clientSocket);
		}
		clientsAccepted++;
	    }
	    catch (IOException e) {
		System.out.println("[ACCEPTOR] Error accepting connection: " + e);
	    }
	}
	System.out.println("[ACCEPTOR] Accepted " + backlog + " clients, exiting");
	try {
	    acceptorSocket.close();
	}
	catch (IOException e){}
    };

    public GraphicsConnection spawnGraphicsHandlerThread(Socket clientSocket){
	System.out.println("Spawning graphics handler!");
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
