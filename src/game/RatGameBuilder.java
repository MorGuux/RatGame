package game;

import game.entity.Entity;
import game.event.GameActionListener;
import game.player.Player;
import gui.game.dependant.tilemap.Coordinates;

import java.io.File;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class RatGameBuilder {

    public RatGameBuilder(File ratGameFile,
                          Player player,
                          GameActionListener listener) {

    }

    public RatGameBuilder(File ratGameFile,
                          GameActionListener listener) {

    }

    public RatGame build() {
        //TODO
        return null;
    }

    private HashMap<Entity, List<Coordinates<Integer>>> loadEntities() {
        //TODO
        return null;
    }

    /**
     * This would pass through the {@link #loadEntities()} Hashmap as a
     * parameter to the {@link RatGameManager}. We don't need to worry about
     * how the Context map is created after this. It will be handled by the
     * Managers construction.
     */
    private void initialiseGameManager() {

    }

    private Player loadPlayer() {
        //TODO
        return null;
    }

    /*private Leaderboard loadLeaderboard() {
        //TODO
        return null;
    }
    */

    /*private ItemGenerator loadGenerator() {
        //TODO
        return null;
    }
    */

}
