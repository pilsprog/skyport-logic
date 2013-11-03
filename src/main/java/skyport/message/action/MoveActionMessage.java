package skyport.message.action;

import skyport.game.Direction;

public class MoveActionMessage extends ActionMessage {
    private Direction direction;
    
    public Direction getDirection() {
        return direction;
    }
}
