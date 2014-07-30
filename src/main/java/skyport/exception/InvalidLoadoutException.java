package skyport.exception;

public class InvalidLoadoutException extends ProtocolException {

    private static final long serialVersionUID = -3067382730110157576L;

    public InvalidLoadoutException(String reason) {
        super(reason);
    }

}
