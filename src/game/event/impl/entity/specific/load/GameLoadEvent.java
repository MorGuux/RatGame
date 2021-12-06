package game.event.impl.entity.specific.load;

import game.event.GameEvent;
import game.level.reader.RatGameFile;
import game.player.Player;
import game.tile.Tile;

/**
 * Game load event wraps the tile map loading and player definition.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class GameLoadEvent extends GameEvent<RatGameFile> {

    // NOTE THAT ENTITY LOADING AND THE ITEM GENERATOR LOADING WILL BE DONE
    // THROUGH A DIFFERENT EVENT.

    /**
     * The player who is playing the rat game.
     */
    private final Player player;

    /**
     * Constructs a game event from a target author.
     *
     * @param author The author of the event.
     * @param activePlayer The player who is playing the game.
     * @throws NullPointerException If the author is a {@code null}.
     */
    public GameLoadEvent(final RatGameFile author,
                         final Player activePlayer) {
        super(author);
        this.player = activePlayer;
    }

    /**
     * @return The number of rows the map has.
     */
    public int getMapRows() {
        return getEventAuthor().getLevel().getRows();
    }

    /**
     * @return The number of columns the map has.
     */
    public int getMapColumns() {
        return getEventAuthor().getLevel().getColumns();
    }

    /**
     * @return The tile map for the level.
     */
    public Tile[][] getTileMap() {
        return getEventAuthor().getLevel().getTiles();
    }

    /**
     * @return The maximum number of rats the level will allow before ending
     * the game.
     */
    public int getMaxRats() {
        return getEventAuthor().getDefaultProperties().getMaxRats();
    }

    /**
     * @return The goal clear time the player should look for in order to be
     * awarded bonus points.
     */
    public int getClearTime() {
        return getEventAuthor().getDefaultProperties().getTimeLimit();
    }

    /**
     * @return The players name.
     */
    public String getPlayerName() {
        return this.player.getPlayerName();
    }

    /**
     * @return The players current score.
     */
    public int getPlayerScore() {
        return this.player.getCurrentScore();
    }

    /**
     * @return The time the player has been playing for.
     */
    public int getPlayerPlayTime() {
        return this.player.getPlayTime();
    }
}
