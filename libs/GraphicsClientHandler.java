import java.util.concurrent.ConcurrentLinkedQueue;
public class GraphicsClientHandler implements Runnable {
    GraphicsConnection connection;
    public GraphicsClientHandler(GraphicsConnection graphicsConnection) {
	connection = graphicsConnection;
	System.out.println("[GRAPHICS] Created new reader thread. ");
    }
    @Override
    public void run(){
	while(true){
	    boolean data = connection.read();
	    if(!data){
		System.out.println("[GRAPHICS] Disconnect from " + connection.getIp() + ". ");
		return;
	    }
	}
    }
}
