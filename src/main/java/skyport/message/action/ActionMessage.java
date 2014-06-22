package skyport.message.action;

import skyport.exception.ProtocolException;
import skyport.game.Player;
import skyport.message.Message;

public abstract class ActionMessage extends Message {
    protected String type;  
    protected String from;

    public ActionMessage() {
        this.message = "action";
    }

    public String getType() {
        return type;
    }

    public void setFrom(String name) {
        this.from = name;
    }

    public void performAction(Player player) throws ProtocolException {
        throw new ProtocolException("Invalid action: '" + type + "'.");
    }
}
