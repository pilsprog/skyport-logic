package skyport.message.action;

import skyport.game.Player;
import skyport.game.Tile;
import skyport.game.TileType;

public abstract class OffensiveActionMessage extends ActionMessage {

    public void damage(Player dealer, Tile tile, int hitpoints) {
        if(tile.tileType == TileType.SPAWN) {
            return;
        }
        Player reciever = tile.playerOnTile;
        if (reciever == null) {
            return;
        }
        if (reciever.getHealth() <= 0) {
            logger.warn("Player is already dead.");
            return;
        }
        logger.debug("'" + reciever.getName() + "' received " + hitpoints + " damage from '" + dealer.getName() + "'!");
        reciever.damage((int) Math.round(hitpoints + 0.2 * dealer.getTurnsLeft() * hitpoints));
        if (!(dealer.equals(reciever))) {
            dealer.givePoints(hitpoints); 
        }
        
        if (reciever.isDead()) {
            logger.info("==> " + reciever.getName() + " got killed by " + dealer.getName() + ".");
            if (!(dealer.equals(reciever))) {
                dealer.givePoints(20); 
            }
            reciever.givePoints(-40);
        }
    }
}
