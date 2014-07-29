package skyport.message;

import skyport.exception.ProtocolException;

public class ErrorMessage extends Message {
    @SuppressWarnings("unused")
    private String error;

    public ErrorMessage(String error) {
        super("error");
        this.error = error;
    }

    public ErrorMessage(ProtocolException e) {
        super("error");
        this.error = e.getMessage();
    }
}
