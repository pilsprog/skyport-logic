package skyport.message.action;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.Point;
import skyport.game.Tile;
import skyport.game.TileType;
import skyport.game.World;
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
    
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void performAction(Player player, World map) throws ProtocolException {
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
           
        List<Point> path = Stream.generate(() -> dir.point)
            .limit(laser.range())
            .collect(Collectors.toList());
        
        Point point = player.getPosition().coords;
        int damage = laser.damage();
        for(Point p : path) {
            point = point.pluss(p);
            Optional<Tile> tile = map.tileAt(point);
            if (tile.map(t -> t.tileType == TileType.ROCK)
                    .orElse(true)) {
                break;
            }
            tile.ifPresent(t -> t.damageTile(damage, player));
        }

        this.stop = point;
    }
    
    @Override
    public String toString() {
        return from + " fired " + type + "in direction " + direction + "."; 
    }
}
