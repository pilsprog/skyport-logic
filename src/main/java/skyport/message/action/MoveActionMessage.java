package skyport.message.action;

import skyport.exception.InaccessibleTileException;
import skyport.exception.ProtocolException;
import skyport.game.Direction;
import skyport.game.Player;
import skyport.game.Tile;
import skyport.game.Vector2d;
import skyport.game.World;

public class MoveActionMessage extends ActionMessage {
    private Direction direction;

    public Direction getDirection() {
        return direction;
    }
    
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    @Override
    public void performAction(Player player, World map) throws ProtocolException {
        Vector2d position = player.getPosition();
        Tile current = map.tileAt(position).get();
        Tile next = map.tileAt(position.plus(direction.vec))
                .orElseThrow(() -> new InaccessibleTileException(direction));
                
                
        if (next.isAccessible()) {
            if (next.playerOnTile != null) {
                throw new ProtocolException("Player " + next.playerOnTile.getName() + " is already on this tile.");
            }
            current.playerOnTile = null; 
            next.playerOnTile = player;
            player.setPosition(next.coords);
        } else {
            throw new InaccessibleTileException(next);
        } 
    }
    
    @Override
    public String toString() {
        return from + " moved in direction " + direction + ".";
    }
}
