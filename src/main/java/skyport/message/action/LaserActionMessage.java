package skyport.message.action;

import java.util.Optional;
import java.util.stream.Stream;

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
    public void performAction(Player player) throws ProtocolException {
        if (player.getPosition().tileType == TileType.SPAWN) {
            throw new ProtocolException("Attempted to shoot laser from spawn.");
        }
        Laser laser = Stream.of(player.primaryWeapon, player.secondaryWeapon)
                .filter(w -> w instanceof Laser)
                .map(w -> (Laser)w)
                .findFirst()
                .orElseThrow(() -> new ProtocolException("Attempted to use laser, but doesn't have it."));

        Direction dir = Optional.ofNullable(direction)
            .orElseThrow(() -> new ProtocolException("Invalid shot: unknown direction '" + direction + "'."));
           
        laser.setDirection(dir);
        laser.setPosition(player.getPosition());
        this.start = player.getPosition().coords;
        this.stop = laser.performShot(player, player.getTurnsLeft());     
    }
    
    @Override
    public String toString() {
        return from + " fired " + type + "in direction " + direction + "."; 
    }
}
