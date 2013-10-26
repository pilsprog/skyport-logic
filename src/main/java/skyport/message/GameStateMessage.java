package skyport.message;

import java.util.List;

import skyport.game.GameMap;
import skyport.game.Player;

public class GameStateMessage extends Message {
    @SuppressWarnings("unused")
    private int turn;
    @SuppressWarnings("unused")
    private GameMap map;
    @SuppressWarnings("unused")
    private List<Player> players;
    
    public GameStateMessage(int turn, GameMap map, List<Player> players) {
        this.message = "gamestate";
        this.turn = turn;
        this.map = map;
        this.players = players;
    }
}
