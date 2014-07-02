package skyport.message.action;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.Vector;
import skyport.game.Tile;
import skyport.game.TileType;
import skyport.game.World;
import skyport.game.weapon.Droid;

public class DroidActionMessage extends ActionMessage implements OffensiveAction {
    private List<Direction> sequence;
    
    private transient final Logger logger = LoggerFactory.getLogger(Droid.class);

    public List<Direction> getPath() {
        return sequence;
    }

    public void setPath(List<Direction> sequence) {
        this.sequence = sequence;
    }

    @Override
    public void performAction(Player player, World map) throws ProtocolException {
        if (player.getPosition().tileType == TileType.SPAWN) {
            throw new ProtocolException("Attempted to shoot droid from spawn.");
        }
        Droid droid = Stream
                .of(player.primaryWeapon, player.secondaryWeapon)
                .filter(w -> w instanceof Droid)
                .map(w -> (Droid)w)
                .findFirst()
                .orElseThrow(() ->
                    new ProtocolException("Attempted to shoot the droid, but doesn't have it."));
        
        List<Vector> path = sequence.stream()
            .limit(droid.radius())
            .map(d -> d.vector)
            .collect(Collectors.toList());
        
        logger.info("==> '" + player.getName() + "' performing droid shot with " + path.size() + " steps.");
        
        Vector vector = player.getPosition().coords;
        for (Vector p : path) {
            vector = vector.plus(p);
            if(!map.tileAt(p)
                   .map(Tile::isAccessible)
                   .orElse(false)) {
                logger.info("Droid hit inaccessible tile.");
               break;
            }
        }
        
        logger.info("Droid detonating.");
        final Vector stop = vector;
        
        int damage = droid.damage();
        int aoe = droid.aoe();
        map.tileAt(stop).ifPresent(tile -> {
            tile.damageTile(damage, player); 
            Stream.of(Direction.values())
                .map(d -> d.vector)
                .map(v -> stop.plus(v))
                .forEach(p -> 
                    map.tileAt(p)
                       .ifPresent(t -> 
                           t.damageTile(aoe, player)));
        });    
    }
    
    @Override
    public String toString() {
        String out = from + " fired " + type + " in sequence ";
        out += Arrays.toString(sequence.toArray());
        return out + ".";
    }
}
