package skyport.message;

import java.util.List;

import skyport.game.Player;
import skyport.game.World;

public class GameStateMessage extends Message {
    @SuppressWarnings("unused")
    private int turn;
    @SuppressWarnings("unused")
    private World map;
    @SuppressWarnings("unused")
    private List<Player> players;

    public GameStateMessage(int turn, World map, List<Player> players) {
        super("gamestate");
        this.turn = turn;
        this.map = map;
        this.players = players;
    }
}
