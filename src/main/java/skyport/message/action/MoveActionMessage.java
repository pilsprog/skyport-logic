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
    
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void performAction(Player player, World map) throws ProtocolException {
        player.move(direction, map);
        
    }
    
    @Override
    public String toString() {
        return from + " moved in direction " + direction + ".";
    }
}
