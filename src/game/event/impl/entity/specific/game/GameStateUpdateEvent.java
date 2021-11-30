package game.event.impl.entity.specific.game;

import game.RatGame;
import game.event.GameEvent;

/**
 * Event informs of a generic game state change. Mainly serves to tell the
 * listener of the new Number of hostile entities and the time elapsed.
 *
 * @author -Ry
 * @version 0.2
 * Copyright: N/A
 */
public class GameStateUpdateEvent extends GameEvent<RatGame> {

    private final int numHostileEntities;

    private final int clearTimeRemaining;

    /**
     * Constructs a game event from a target author.
     *
     * @param author The author of the event.
     * @throws NullPointerException If the author is a {@code null}.
     */
    public GameStateUpdateEvent(final RatGame author,
                                final int hostileEntityCount,
                                final int timeRemaining) {
        super(author);
        this.numHostileEntities = hostileEntityCount;
        this.clearTimeRemaining = timeRemaining;
    }

    /**
     * @return The number of hostile entities in the game.
     */
    public int getNumHostileEntities() {
        return numHostileEntities;
    }

    /**
     * @return The clear time remaining in order to be awarded bonus points.
     */
    public int getClearTimeRemaining() {
        return clearTimeRemaining;
    }

    /**
     * @return Gets the clear time remaining in seconds.
     */
    public double getClearTimeSeconds() {
        final double timeFactor = 1000.0;
        return (double) clearTimeRemaining / timeFactor;
    }
}
