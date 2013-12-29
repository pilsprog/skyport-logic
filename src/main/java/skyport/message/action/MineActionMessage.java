package skyport.message.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.Player;
import skyport.game.TileType;

public class MineActionMessage extends ActionMessage {

    private transient final Logger logger = LoggerFactory.getLogger(MineActionMessage.class);

    @Override
    public boolean performAction(Player player) throws ProtocolException {
        TileType currentTileType = player.position.tileType;
        if (!(currentTileType == TileType.RUBIDIUM || currentTileType == TileType.EXPLOSIUM || currentTileType == TileType.SCRAP)) {
            logger.info("==> Player " + player + " attempted to mine while not on a resource");
            throw new ProtocolException("Tried to mine while not on a resource tile!");
        }

        TileType tileType = player.position.tileType;
        logger.info("==> Player " + player + " mining " + tileType);
        boolean minedResource = player.position.mineTile();
        if (minedResource) {
            switch (tileType) {
            case RUBIDIUM:
                player.rubidiumResources++;
                break;
            case EXPLOSIUM:
                player.explosiumResources++;
                break;
            case SCRAP:
                player.scrapResources++;
                break;
            default:
            }
            logger.debug("Resources of player " + player + " are now: Rubidium: " + player.rubidiumResources + ", Explosium: " + player.explosiumResources + ", Scrap: " + player.scrapResources);
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return from + " mined.";
    }
}
