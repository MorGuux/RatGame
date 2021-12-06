package game.level.reader.exception;

/**
 * Exception wraps the case when an essential module in a rat game file does
 * not exist.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class MissingModuleException extends RatGameFileException {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public MissingModuleException(final String message) {
        super(message);
    }
}
