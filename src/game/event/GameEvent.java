package game.event;

import java.util.Objects;

/**
 * Base game event wraps an author object and the ability to obtain said author.
 *
 * @param <T> The type of the author that fired this event.
 */
public abstract class GameEvent<T> {

    /**
     * The author of the event.
     */
    private final T eventAuthor;

    /**
     * Constructs a game event from a target author.
     *
     * @param author The author of the event.
     * @throws NullPointerException If the author is a {@code null}.
     */
    public GameEvent(final T author) {
        Objects.requireNonNull(author);
        this.eventAuthor = author;
    }

    /**
     * @return The event author that fired the event.
     */
    protected T getEventAuthor() {
        return eventAuthor;
    }
}
