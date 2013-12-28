package skyport.message;

public class SubtitleMessage extends Message {
    @SuppressWarnings("unused")
    private String text;

    public SubtitleMessage(String text) {
        this.message = "subtitle";
        this.text = text;
    }
}
