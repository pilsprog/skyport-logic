package skyport.message.action;

import skyport.game.Point;
import skyport.game.Direction;

public class LaserActionMessage extends ActionMessage {
    private Direction direction;
    @SuppressWarnings("unused")
    private Point start;
    @SuppressWarnings("unused")
    private Point stop;

    public void setInterval(Point startHack, Point stopHack) {
        this.start = startHack;
        this.stop = stopHack;
    }
    
    public Direction getDirection() {
        return direction;
    }
}
