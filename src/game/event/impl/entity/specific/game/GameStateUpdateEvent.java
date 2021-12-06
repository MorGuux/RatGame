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

    /**
     * Number of hostile entities on the map (aka rats, except for death rats).
     */
    private final int numHostileEntities;
    /**
     * Number of Male rats left.
     */
    private final int numMaleHostileEntities;
    /**
     * Number of Female rats left.
     */
    private final int numFemaleHostileEntities;
    /**
     * Time left on the clock (used to calculate score).
     */
    private final int clearTimeRemaining;

    /**
     * Constructs a game event from a target author.
     *
     * @param author The author of the event.
     * @param hostileEntityCount Number of Rats on the level.
     * @param hostileMaleEntityCount Number of Male rats on the level.
     * @param hostileFemaleEntityCount Number of Female rats on the level.
     * @param timeRemaining Time remaining for the purposes of score.
     * @throws NullPointerException If the author is a {@code null}.
     */
    public GameStateUpdateEvent(final RatGame author,
                                final int hostileEntityCount,
                                final int hostileMaleEntityCount,
                                final int hostileFemaleEntityCount,
                                final int timeRemaining) {
        super(author);
        this.numHostileEntities = hostileEntityCount;
        this.numMaleHostileEntities = hostileMaleEntityCount;
        this.numFemaleHostileEntities = hostileFemaleEntityCount;
        this.clearTimeRemaining = timeRemaining;
    }

    /**
     * @return The number of hostile entities in the game.
     */
    public int getNumHostileEntities() {
        return numHostileEntities;
    }

    /**
     * @return The number of female hostile entities in the game.
     */
    public int getNumFemaleHostileEntities() {
        return numFemaleHostileEntities;
    }

    /**
     * @return The number of male hostile entities in the game.
     */
    public int getNumMaleHostileEntities() {
        return numMaleHostileEntities;
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
