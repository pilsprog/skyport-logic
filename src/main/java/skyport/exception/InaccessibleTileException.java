package skyport.exception;

import skyport.game.Direction;
import skyport.game.Tile;

public class InaccessibleTileException extends ProtocolException {
    private static final long serialVersionUID = 4316436111575102158L;

    public InaccessibleTileException(Direction direction) {
        super("Invalid move: tile " + direction + " is not accessible (outside of map)");
    }

    public InaccessibleTileException(Tile tile) {
        super("Invalid move: " + tile.tileType + " tile is not accessible");
    }

}
