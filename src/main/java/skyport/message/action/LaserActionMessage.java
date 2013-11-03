package skyport.message.action;

import skyport.game.Coordinate;
import skyport.game.Direction;

public class LaserActionMessage extends ActionMessage {
    @SuppressWarnings("unused")
    private Direction direction;
    @SuppressWarnings("unused")
    private Point start;
    @SuppressWarnings("unused")
    private Point stop;

    public void setInterval(Point startHack, Point stopHack) {
        this.start = startHack;
        this.stop = stopHack;
    }
}
