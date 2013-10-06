package skyport.exception;

public class ProtocolException extends Exception {
    private static final long serialVersionUID = 8760793291740761986L;

    public ProtocolException(String reason) {
        super(reason);
    }
}
