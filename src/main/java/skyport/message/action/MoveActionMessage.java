package skyport.message.action;

import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.World;

public class MoveActionMessage extends ActionMessage {
    private Direction direction;

    public Direction getDirection() {
        return direction;
    }

    @Override
    public void performAction(Player player, World map) throws ProtocolException {
        player.move(direction);
    }
    
    @Override
    public String toString() {
        return from + " moved in direction " + direction + ".";
    }
}
