public class GraphicsContainer {
    // could be extended to a multicontainer or so, if needed
    private GraphicsConnection graphics = null;
    public synchronized GraphicsConnection get(){
	return graphics;
    }
    public synchronized void set(GraphicsConnection connection){
	graphics = connection;
    }
}
