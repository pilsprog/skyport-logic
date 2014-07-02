package skyport.message.action;

import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.Vector;
import skyport.game.TileType;
import skyport.game.World;
import skyport.game.weapon.Mortar;

public class MortarActionMessage extends ActionMessage implements OffensiveAction {
    private Vector coordinates;
    
    public void setCoordinates(Vector coords) {
        this.coordinates = coords;
    }
    
    private transient final Logger logger = LoggerFactory.getLogger(MortarActionMessage.class);

    @Override
    public void performAction(Player player, World map) throws ProtocolException {
        if (player.getPosition().tileType == TileType.SPAWN) {
            throw new ProtocolException("Attempted to shoot mortar from spawn.");
        }
        
        Mortar mortar = Stream.of(player.primaryWeapon, player.secondaryWeapon)
                .filter(w -> w instanceof Mortar)
                .map(w -> (Mortar)w)
                .findFirst()
                .orElseThrow(() -> new ProtocolException("Attempted to shoot the mortar, but doesn't have it."));
        
        if(!isTileInRange(mortar.range())) {
            throw new ProtocolException("Relative coordinates " + coordinates + " are out of range.");
        }
        
        Vector target = player.getPosition().coords.plus(coordinates);
        int damage = mortar.damage();
        int aoe = mortar.aoe();
        map.tileAt(target).ifPresent(tile -> {
            tile.damageTile(damage, player);
            
            Stream.of(Direction.values())
                .map(d -> d.vector)
                .map(p -> target.plus(p))
                .forEach(p -> 
                    map.tileAt(p).ifPresent(t -> 
                        t.damageTile(aoe, player)));
        });
    }
    
    private boolean isTileInRange(int range) {
        // simple approximative method derived from the cosine law, works
        // correctly for distances < ~8 tiles or so. Good enough for this game.
        int j = coordinates.j;
        int k = coordinates.k;
        assert (range < 8);
        int shotRange = (int) Math.ceil(Math.sqrt(Math.pow(j, 2) + Math.pow(k, 2) - 2 * j * k * 0.5));
        logger.debug("shot range was: " + shotRange);
        return shotRange <= range;
    }
    
    @Override
    public String toString() {
        return from + " fired " + type + " at " + coordinates + ".";
    }
}
