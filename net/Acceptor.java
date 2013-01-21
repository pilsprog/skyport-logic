import java.net.*;
import java.io.*;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Acceptor implements Runnable {
    private ServerSocket acceptorSocket;
    private ConcurrentLinkedQueue<AIConnection> globalClientList;
    public Acceptor(int port, ConcurrentLinkedQueue<AIConnection> globalClients){
	try {
	    acceptorSocket = new ServerSocket(port);
	    acceptorSocket.setReuseAddress(true);
	    globalClientList = globalClients;
	    System.out.println("[ACCEPTOR] listening on port " + acceptorSocket.toString());
	}
	catch(IOException e){
	    System.out.println("[ACCEPTOR] Error binding to port: " + e);
	    System.exit(1);
	}
    }
    public void run(){
	System.out.println("[ACCEPTOR] Starting to accept incoming connections");
	while(true){
	    try {
		Socket clientSocket = acceptorSocket.accept();
		System.out.println("[ACCEPTOR] Connect from " + clientSocket);
		spawnReadHandlerThread(clientSocket);
	    }
	    catch (IOException e) {
		System.out.println("[ACCEPTOR] Error accepting connection: " + e);
	    }
	}
    };

    public void spawnReadHandlerThread(Socket clientSocket){
	AIConnection aiConnectionObject = new AIConnection(clientSocket);
	globalClientList.add(aiConnectionObject);
	AIClientHandler handler = new AIClientHandler(aiConnectionObject, globalClientList);
	Thread thread = new Thread(handler);
	thread.start();
    }
}
