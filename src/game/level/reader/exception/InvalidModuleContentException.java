package game.level.reader.exception;

public class InvalidModuleContentException extends RatGameFileException {

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for
     *                later retrieval by the {@link #getMessage()} method.
     */
    public InvalidModuleContentException(final String message) {
        super(message);
    }
}
