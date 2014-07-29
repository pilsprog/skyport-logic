package skyport.message;

public class StatusMessage extends Message {
    @SuppressWarnings("unused")
    private boolean status;

    public StatusMessage(boolean status) {
        super("connect");
        this.status = status;
    }
}
