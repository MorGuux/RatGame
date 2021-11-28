package game.level.reader.exception;

/**
 * Exception wraps the case when the entire data is just invalid and cannot
 * be parsed into the data it's supposed to represent.
 *
 * @author -Ry
 * @version 0.1
 * Copyrigh: N/A
 */
public class ImproperlyFormattedArgs extends RatGameFileException {
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public ImproperlyFormattedArgs(String message) {
        super(message);
    }
}
