package skyport.message.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.Point;
import skyport.game.TileType;
import skyport.game.weapon.Laser;

public class LaserActionMessage extends ActionMessage implements OffensiveAction {
    private Direction direction;
    @SuppressWarnings("unused")
    private Point start;
    @SuppressWarnings("unused")
    private Point stop;

    private transient final Logger logger = LoggerFactory.getLogger(LaserActionMessage.class);

    public void setInterval(Point startHack, Point stopHack) {
        this.start = startHack;
        this.stop = stopHack;
    }

    public Direction getDirection() {
        return direction;
    }

    @Override
    public boolean performAction(Player player) throws ProtocolException {
        if (player.position.tileType == TileType.SPAWN) {
            logger.info("==> Player attempted to shoot laser from spawn.");
            return false;
        }
        Laser laser;
        if (player.primaryWeapon.getName().equals("laser")) {
            laser = (Laser) player.primaryWeapon;
        } else if (player.secondaryWeapon.getName().equals("laser")) {
            laser = (Laser) player.secondaryWeapon;
        } else {
            return false;
        }

        if (direction != null) {
            laser.setDirection(direction);
            laser.setPosition(player.position);
            this.start = player.position.coords;
            this.stop = laser.performShot(player, player.getTurnsLeft());
            return true;
        } else {
            throw new ProtocolException("Invalid shot: unknown direction '" + direction + "'.");
        }
    }
    
    @Override
    public String toString() {
        return from + " fired " + type + "in direction " + direction + "."; 
    }
}
