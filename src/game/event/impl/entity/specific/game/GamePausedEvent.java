package game.event.impl.entity.specific.game;

import game.RatGame;
import game.event.GameEvent;

/**
 * Represents when the rat game is paused. Primarily used as a flag event to
 * indicate a state change.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class GamePausedEvent extends GameEvent<RatGame> {

    /**
     * Constructs a game event from a target author.
     *
     * @param author The author of the event.
     * @throws NullPointerException If the author is a {@code null}.
     */
    public GamePausedEvent(final RatGame author) {
        super(author);
    }

    /**
     * @return The event author that fired the event.
     */
    @Override
    public RatGame getEventAuthor() {
        return super.getEventAuthor();
    }
}
