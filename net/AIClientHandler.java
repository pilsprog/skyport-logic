import java.util.concurrent.ConcurrentLinkedQueue;
public class AIClientHandler implements Runnable {
    AIConnection connection;
    ConcurrentLinkedQueue<AIConnection> globalClientList;
    public AIClientHandler(AIConnection aiConnection, ConcurrentLinkedQueue<AIConnection> lq) {
	connection = aiConnection;
	globalClientList = lq;
	System.out.println("[AIREADER] Created new reader thread. " + lq.size() + " clients active.");
    }
    @Override
    public void run(){
	while(true){
	    boolean data = connection.read();
	    if(!data){
		globalClientList.remove(connection);
		System.out.println("[AIREADER] Disconnect from " + connection.getIp() + ". "
				   + globalClientList.size() + " clients active.");
		return;
	    }
	}
    }
}
