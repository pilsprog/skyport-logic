package skyport.message.action;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.Tile;
import skyport.game.TileType;
import skyport.game.Vector2d;
import skyport.game.World;
import skyport.game.weapon.Laser;

public class LaserActionMessage extends  OffensiveActionMessage {
    private Direction direction;
    @SuppressWarnings("unused")
    private Vector2d start;
    @SuppressWarnings("unused")
    private Vector2d stop;

    public void setInterval(Vector2d startHack, Vector2d stopHack) {
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
        if (map.tileAt(player.getPosition())
               .filter(t -> t.tileType == TileType.SPAWN)
               .isPresent()) {
            throw new ProtocolException("Attempted to shoot laser from spawn.");
        }
  
        Laser laser = Stream.of(player.getPrimaryWeapon(), player.getSecondaryWeapon())
                .filter(w -> w instanceof Laser)
                .map(w -> (Laser)w)
                .findFirst()
                .orElseThrow(() -> new ProtocolException("Attempted to use laser, but doesn't have it."));

        Direction dir = Optional.ofNullable(direction)
            .orElseThrow(() -> new ProtocolException("Invalid shot: unknown direction '" + direction + "'."));
           
        List<Vector2d> path = Stream.generate(() -> dir.vec)
            .limit(laser.range())
            .collect(Collectors.toList());
        
        Vector2d vector = player.getPosition();
        int damage = laser.damage();
        for(Vector2d p : path) {
            vector = vector.plus(p);
            Optional<Tile> tile = map.tileAt(vector);
            if (tile.map(t -> t.tileType == TileType.ROCK)
                    .orElse(true)) {
                break;
            }
            tile.ifPresent(t -> this.damage(player, t, damage));
        }

        this.stop = vector;
    }
    
    @Override
    public String toString() {
        return from + " fired " + type + "in direction " + direction + "."; 
    }
}
