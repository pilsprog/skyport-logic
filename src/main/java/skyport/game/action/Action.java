package skyport.game.action;

import skyport.message.Message;

public class Action extends Message {
    protected String type;
    
    public Action() {
        this.message = "action";
    }
}
