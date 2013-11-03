package skyport.message.action;

import skyport.message.Message;

public class ActionMessage extends Message {
    protected String type;
    @SuppressWarnings("unused")
    private String from;
    
    public ActionMessage() {
        this.message = "action";
    }

    public String getType() {
        return type;
    }
    
    public void setFrom(String name) {
        this.from = name;
    }
}
