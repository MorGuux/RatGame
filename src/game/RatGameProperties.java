package game;

import game.event.GameActionListener;
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

    //private ItemGenerator itemGenerator;
    private GameActionListener actionListener;
    private Player player;
    //private Leaderboard leaderboard;

    public RatGameProperties(GameActionListener actionListener, Player player) {
        this.actionListener = actionListener;
        this.player = player;
    }



    public GameActionListener getActionListener() {
        return actionListener;
    }

    public Player getPlayer() {
        return player;
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
