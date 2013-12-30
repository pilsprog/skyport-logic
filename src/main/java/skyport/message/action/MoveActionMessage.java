package skyport.message.action;

import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;

public class MoveActionMessage extends ActionMessage {
    private Direction direction;

    public Direction getDirection() {
        return direction;
    }

    @Override
    public boolean performAction(Player player) throws ProtocolException {
        return player.move(direction);
    }
    
    @Override
    public String toString() {
        return from + " moved in direction " + direction + ".";
    }
}
