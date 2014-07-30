package skyport.message;

@SuppressWarnings("unused")
public class InfoMessage extends Message {   
    private long timeout;
    private long rounds;
    private String map;
    private int players;
    

    public InfoMessage(long timeout, long rounds, String map, int players) {
        super("info");
        this.timeout = timeout;
        this.rounds = rounds;
        this.map = map;
        this.players = players;
    }
}
