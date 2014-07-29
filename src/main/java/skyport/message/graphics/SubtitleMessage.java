package skyport.message.graphics;

import skyport.message.Message;

public class SubtitleMessage extends Message {
    @SuppressWarnings("unused")
    private String text;

    public SubtitleMessage(String text) {
        super("subtitle");
        this.text = text;
    }
}
