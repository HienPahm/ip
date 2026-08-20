/**
 * Represents an error specific to the Will chatbot, e.g. an invalid or
 * incomplete command entered by the user.
 */
public class WillException extends Exception {
    public WillException(String message) {
        super(message);
    }
}
