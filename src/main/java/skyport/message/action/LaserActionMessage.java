package skyport.message.action;

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
            throw new ProtocolException("Attempted to shoot laser from spawn.");
        }
        Laser laser;
        if (player.primaryWeapon.getName().equals("laser")) {
            laser = (Laser) player.primaryWeapon;
        } else if (player.secondaryWeapon.getName().equals("laser")) {
            laser = (Laser) player.secondaryWeapon;
        } else {
            throw new ProtocolException("Attemted to use laser, but doesn't have it.");
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
