package skyport.message;

public class TitleMessage extends Message {
    @SuppressWarnings("unused")
    private String text;
    
    public TitleMessage(String text) {
        this.message = "title";
        this.text = text;
    }
}
