package skyport.message.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.Player;
import skyport.game.Tile;
import skyport.game.TileType;
import skyport.game.World;

public class MineActionMessage extends ActionMessage {

    private transient final Logger logger = LoggerFactory.getLogger(MineActionMessage.class);

    @Override
    public void performAction(Player player, World map) throws ProtocolException {
        Tile tile = map.tileAt(player.getPosition())
                .filter(t -> t.tileType == TileType.RUBIDIUM  || t.tileType == TileType.EXPLOSIUM || t.tileType == TileType.SCRAP)
                .orElseThrow(() -> {
                    logger.info("==> Player " + player + " attempted to mine while not on a resource.");  
                    return new ProtocolException("Tried to mine while not on a resource tile!");
                });

        logger.info("==> Player " + player.getName() + " mining " + tile.tileType);
        TileType tileType = tile.tileType;
        tile.mineTile();       
        switch (tileType) {
        case RUBIDIUM:
            player.addRubidium();
            break;
        case EXPLOSIUM:
            player.addExplosium();
            break;
        case SCRAP:
            player.addScrap();
            break;
        default:
        }
        logger.debug("Resources of player " + player 
                + " are now: Rubidium: " + player.getRubidium()
                       + ", Explosium: " + player.getExplosium()
                           + ", Scrap: " + player.getScrap());
    }

    @Override
    public String toString() {
        return from + " mined.";
    }
}
