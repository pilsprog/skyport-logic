package skyport.message.action;

import skyport.debug.Debug;
import skyport.exception.ProtocolException;
import skyport.game.Player;
import skyport.game.TileType;

public class MineActionMessage extends ActionMessage {

    @Override
    public boolean performAction(Player player) throws ProtocolException {
        TileType currentTileType = player.position.tileType;
        if (!(currentTileType == TileType.RUBIDIUM || currentTileType == TileType.EXPLOSIUM || currentTileType == TileType.SCRAP)) {
            Debug.game("Player " + player.name + " attempted to mine while not on a resource");
            throw new ProtocolException("Tried to mine while not on a resource tile!");       
        }
        
        TileType tileType = player.position.tileType;
        Debug.game("Player " + player.name + " mining " + tileType);
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
            Debug.debug("Resources of player " + player.name + 
                    " are now: Rubidium: " + player.rubidiumResources + 
                           ", Explosium: " + player.explosiumResources + 
                               ", Scrap: " + player.scrapResources);
            return true;
        }
        return false;
    }
}
