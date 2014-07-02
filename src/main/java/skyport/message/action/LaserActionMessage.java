package skyport.message.action;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.Vector;
import skyport.game.Tile;
import skyport.game.TileType;
import skyport.game.World;
import skyport.game.weapon.Laser;

public class LaserActionMessage extends ActionMessage implements OffensiveAction {
    private Direction direction;
    @SuppressWarnings("unused")
    private Vector start;
    @SuppressWarnings("unused")
    private Vector stop;

    public void setInterval(Vector startHack, Vector stopHack) {
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
           
        List<Vector> path = Stream.generate(() -> dir.vector)
            .limit(laser.range())
            .collect(Collectors.toList());
        
        Vector vector = player.getPosition().coords;
        int damage = laser.damage();
        for(Vector p : path) {
            vector = vector.plus(p);
            Optional<Tile> tile = map.tileAt(vector);
            if (tile.map(t -> t.tileType == TileType.ROCK)
                    .orElse(true)) {
                break;
            }
            tile.ifPresent(t -> t.damageTile(damage, player));
        }

        this.stop = vector;
    }
    
    @Override
    public String toString() {
        return from + " fired " + type + "in direction " + direction + "."; 
    }
}
