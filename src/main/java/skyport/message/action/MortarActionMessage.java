package skyport.message.action;

import skyport.game.Point;

public class MortarActionMessage extends ActionMessage {
    private Point coordinates;
    
    public Point getRelativeTarget() {
        return coordinates;
    }
}
