package skyport.message.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyport.exception.ProtocolException;
import skyport.game.Player;
import skyport.game.World;
import skyport.message.Message;

public abstract class ActionMessage extends Message {
    protected String type;  
    protected String from;
    
    protected transient final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    public ActionMessage() {
        super("action");
    }

    public String getType() {
        return type;
    }

    public void setFrom(String name) {
        this.from = name;
    }

    public void performAction(Player player, World map) throws ProtocolException {
        throw new ProtocolException("Invalid action: '" + type + "'.");
    }
}
