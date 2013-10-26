package skyport.game.action;

import skyport.game.Coordinate;
import skyport.game.Direction;

public class LaserAction extends Action {
    @SuppressWarnings("unused")
    private Direction direction;
    @SuppressWarnings("unused")
    private Coordinate start;
    @SuppressWarnings("unused")
    private Coordinate stop;

    public void setInterval(Coordinate startHack, Coordinate stopHack) {
        this.start = startHack;
        this.stop = stopHack;
    }
}
