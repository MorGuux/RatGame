package game.level.reader.exception;

/**
 * Exception wraps a state in which a Rat Game File has two mappings for a
 * single value.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class DuplicateModuleException extends Exception {

    /**
     * Constructs a new exception with {@code null} as its detail message.
     * The cause is not initialized, and may subsequently be initialized by a
     * call to {@link #initCause}.
     */
    public DuplicateModuleException(final String error) {
        super(error);
    }
}
