package skyport.message;

public class ErrorMessage extends Message {
    @SuppressWarnings("unused")
    private String error;

    public ErrorMessage(String error) {
        this.error = error;
    }
}
