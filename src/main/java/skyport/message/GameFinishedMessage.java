package skyport.message;

public class GameFinishedMessage extends Message {
    String winner;
    public GameFinishedMessage(String winner) {
        super("endactions");
        this.winner = winner;
    }
}
