package game.event.impl.entity.specific.game;

import game.RatGame;
import game.event.GameEvent;

/**
 * Wraps the end of the rat game.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class GameEndEvent extends GameEvent<RatGame> {

    /**
     * Constructs a game event from a target author.
     *
     * @param author The author of the event.
     * @throws NullPointerException If the author is a {@code null}.
     */
    public GameEndEvent(RatGame author) {
        super(author);
    }

    /**
     * @return {@code true} if the player has won the game. {@code false} if
     * they have lost the game.
     */
    public boolean isGameWon() {
        return getEventAuthor().isGameWon();
    }
}
