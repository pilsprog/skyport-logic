package skyport.message.action;

import skyport.message.Message;

public class ActionMessage extends Message {
    protected String type;
    
    public ActionMessage() {
        this.message = "action";
    }
}
