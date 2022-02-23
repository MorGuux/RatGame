package game;

import game.event.GameActionListener;
import game.generator.RatItemInventory;
import game.level.reader.RatGameFile;
import game.level.reader.RatGameSaveFile;
import game.player.Player;
import game.player.leaderboard.Leaderboard;

/**
 * Rat Game Properties gives access to the properties of the game. This
 * includes the player, the item generator, the action listener and the
 * leaderboard.
 *
 * @author Morgan Gardner
 * @version 0.1
 * Copyright: N/A
 */
public class RatGameProperties {

    /**
     * Item pool generator setup ready to be used for the game.
     */
    private final RatItemInventory itemGenerator;

    /**
     * Event handler target which will take rat game events.
     */
    private final GameActionListener actionListener;

    /**
     * Maximum number of hostile entities allowed before the game ends.
     */
    private final int maxHostileEntities;

    /**
     * The player whose playing the rat game.
     */
    private final Player player;

    /**
     * The place where we will save all the player information.
     */
    private final RatGameSaveFile savePoint;

    /**
     * The default level file used to create and load the game.
     */
    private final RatGameFile defaultLevelFile;

    /**
     * The clear time expected in order to be awarded bonus points.
     */
    private final int expectedClearTime;

    /**
     * Leaderboard for all the players who have played the Rat game.
     */
    private final Leaderboard leaderboard;


    /**
     * @param eventHandler          Handler that takes a game event.
     * @param itemGenerator         Item generator handler that handles the
     *                              generation of game events.
     * @param maxHostileEntityCount Maximum number of hostile entities before
     *                              the game ends in a loss.
     * @param leaderboard           Leaderboard of players who've completed
     *                              the level being played.
     * @param expectedClearTime     The expected clear time for the level.
     * @param player                Player who is playing the game.
     * @param defaultFile           The default RatGameFile.
     * @param savePoint             Save point for the player.
     */
    public RatGameProperties(final GameActionListener eventHandler,
                             final RatItemInventory itemGenerator,
                             final int maxHostileEntityCount,
                             final Leaderboard leaderboard,
                             final int expectedClearTime,
                             final Player player,
                             final RatGameFile defaultFile,
                             final RatGameSaveFile savePoint) {
        this.actionListener = eventHandler;
        this.itemGenerator = itemGenerator;
        this.maxHostileEntities = maxHostileEntityCount;
        this.player = player;
        this.leaderboard = leaderboard;
        this.defaultLevelFile = defaultFile;
        this.expectedClearTime = expectedClearTime;
        this.savePoint = savePoint;
    }

    /**
     * @return Event handler.
     */
    public GameActionListener getActionListener() {
        return actionListener;
    }

    /**
     * @return Current player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return The maximum allowed number of hostile entities for the game.
     */
    public int getMaxHostileEntities() {
        return this.maxHostileEntities;
    }

    /**
     * @return Item generator for the level.
     */
    public RatItemInventory getItemGenerator() {
        return itemGenerator;
    }

    /**
     * @return Leaderboard of players.
     */
    public Leaderboard getLeaderboard() {
        return leaderboard;
    }

    /**
     * @return The expected clear time for the level.
     */
    public int getExpectedClearTime() {
        return expectedClearTime;
    }

    /**
     * @return The save point for the player.
     */
    public RatGameSaveFile getSavePoint() {
        return savePoint;
    }

    /**
     * @return The default rat game file.
     */
    public RatGameFile getDefaultLevelFile() {
        return defaultLevelFile;
    }
}
