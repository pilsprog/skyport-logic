package skyport.message.action;

import java.util.regex.Pattern;

import skyport.exception.ProtocolException;
import skyport.game.Player;
import skyport.game.World;


public class HandshakeMessage extends ActionMessage {
    private int revision;
    private String name;

    public int getRevision() {
        return revision;
    }

    public String getName() {
        return name;
    }
    
    public void performAction(Player player, World map) throws ProtocolException {
        if (this.revision != 1) {
            throw new ProtocolException("Wrong protocol revision: supporting 1, but got " + revision + ".");
        }

        if (name.length() < 3) {
            throw new ProtocolException("Username too short: needs to be 3 characters or longer.");
        }
        
        if (name.length() > 16) {
            throw new ProtocolException("Username too long: needs to be 16 characters or less.");
        }
        
        if (!Pattern.matches("[a-zA-Z0-9-_+]+", name)) {
            throw new ProtocolException("Username contains invalid characters. May only contain " + "a-z, A-Z, 0-9, -, _, +.");
        }
        
        player.setName(name);
    }
}
