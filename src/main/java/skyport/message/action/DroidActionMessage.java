package skyport.message.action;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.Tile;
import skyport.game.TileType;
import skyport.game.Vector2d;
import skyport.game.World;
import skyport.game.weapon.Droid;

public class DroidActionMessage extends OffensiveActionMessage {
    private List<Direction> sequence;
    

    public List<Direction> getPath() {
        return sequence;
    }

    public void setPath(List<Direction> sequence) {
        this.sequence = sequence;
    }

    @Override
    public void performAction(Player player, World map) throws ProtocolException {
        if (map.tileAt(player.getPosition())
                .filter(t -> t.tileType == TileType.SPAWN)
                .isPresent()) {
             throw new ProtocolException("Attempted to shoot droid from spawn.");
         }
        
        Droid droid = Stream
                .of(player.getPrimaryWeapon(), player.getSecondaryWeapon())
                .filter(w -> w instanceof Droid)
                .map(w -> (Droid)w)
                .findFirst()
                .orElseThrow(() ->
                    new ProtocolException("Attempted to shoot the droid, but doesn't have it."));
        
        List<Vector2d> path = sequence.stream()
            .limit(droid.radius())
            .map(d -> d.vec)
            .collect(Collectors.toList());
        
        logger.info("==> '" + player.getName() + "' performing droid shot with " + path.size() + " steps.");
        
        Vector2d vector = player.getPosition();
        for (Vector2d p : path) {
            vector = vector.plus(p);
            if(!map.tileAt(p)
                   .map(Tile::isAccessible)
                   .orElse(false)) {
                logger.info("Droid hit inaccessible tile.");
               break;
            }
        }
        
        logger.info("Droid detonating.");
        final Vector2d stop = vector;
        
        int damage = droid.damage();
        int aoe = droid.aoe();
        map.tileAt(stop).ifPresent(tile -> {
            this.damage(player, tile, damage);
            Stream.of(Direction.values())
                .map(d -> d.vec)
                .map(v -> stop.plus(v))
                .forEach(p -> 
                    map.tileAt(p)
                       .ifPresent(t -> 
                           this.damage(player, t, aoe)));
        });    
    }
    
    @Override
    public String toString() {
        String out = from + " fired " + type + " in sequence ";
        out += Arrays.toString(sequence.toArray());
        return out + ".";
    }
}
