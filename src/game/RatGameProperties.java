package game;

import game.event.GameActionListener;
import game.generator.RatItemGenerator;
import game.player.Player;

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

    private RatItemGenerator itemGenerator;
    private GameActionListener actionListener;
    private final int maxHostileEntities;
    private Player player;
    //private Leaderboard leaderboard;

    public RatGameProperties(final GameActionListener eventHandler,
                             final RatItemGenerator itemGenerator,
                             final int maxHostileEntityCount,
                             final Player player) {
        this.actionListener = eventHandler;
        this.itemGenerator = itemGenerator;
        this.maxHostileEntities = maxHostileEntityCount;
        this.player = player;
    }

    public GameActionListener getActionListener() {
        return actionListener;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * @return The maximum allowed number of hostile entities for the game.
     */
    public int getMaxHostileEntities() {
        return this.maxHostileEntities;
    }

    /*public ItemGenerator getItemGenerator() {
        return itemGenerator;
    }
    */

    /*public Leaderboard getLeaderboard() {
        return leaderboard;
    }
     */
}
