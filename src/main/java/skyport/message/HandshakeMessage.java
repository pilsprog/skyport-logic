package skyport.message;

public class HandshakeMessage extends Message {
    private int revision;
    private String name;
    
    public int getRevision() {
        return revision;
    }
    
    public String getName() {
        return name;
    }
}
