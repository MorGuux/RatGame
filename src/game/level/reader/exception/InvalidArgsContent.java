package game.level.reader.exception;

/**
 * Exception wraps when some content held within a Rat Game File has an
 * improper value that cannot be evaluated.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class InvalidArgsContent extends RatGameFileException {
    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public InvalidArgsContent(String message) {
        super(message);
    }
}
