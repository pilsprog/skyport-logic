package skyport.message;

public class Message {
    protected String message;

    public Message() {};

    public Message(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
