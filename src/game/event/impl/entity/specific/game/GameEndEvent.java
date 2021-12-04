package game.event.impl.entity.specific.game;

import game.RatGame;
import game.RatGameProperties;
import game.event.GameEvent;

/**
 * Wraps the end of the rat game.
 *
 * @author -Ry
 * @version 0.2
 * Copyright: N/A
 */
public class GameEndEvent extends GameEvent<RatGame> {

    /**
     *
     */
    private final RatGameProperties properties;

    /**
     * Constructs a game event from a target author.
     *
     * @param author The author of the event.
     * @throws NullPointerException If the author is a {@code null}.
     */
    public GameEndEvent(final RatGame author,
                        final RatGameProperties properties) {
        super(author);
        this.properties = properties;
    }

    /**
     * @return The game that has concluded.
     */
    @Override
    public RatGame getEventAuthor() {
        return super.getEventAuthor();
    }

    /**
     * @return {@code true} if the player has won the game. {@code false} if
     * they have lost the game.
     */
    public boolean isGameWon() {
        return getEventAuthor().isGameWon();
    }

    /**
     * @return The game properties of the game that has just finished.
     */
    public RatGameProperties getProperties() {
        return properties;
    }
}
