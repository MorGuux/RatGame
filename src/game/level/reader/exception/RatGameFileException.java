package game.level.reader.exception;

/**
 * Class wraps any and all issues that a RatGameFile may have from things
 * such as duplicate tags, duplicate modules, missing modules and improperly
 * formatted data.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class RatGameFileException extends Exception {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public RatGameFileException(String message) {
        super(message);
    }
}
