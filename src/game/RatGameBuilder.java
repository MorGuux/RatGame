package game;

import game.entity.Entity;
import game.event.GameActionListener;
import game.generator.ItemGenerator;
import game.player.Player;
import game.player.leaderboard.Leaderboard;
import gui.game.dependant.tilemap.Coordinates;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * This class is responsible for building a game from a given level file,
 * player and event listener. It will construct the necessary dependencies
 * including the leaderboard, item generator, and entity list.
 *
 * @author Morgan Gardner
 * @version 0.1
 * Copyright: N/A
 */
public class RatGameBuilder {


    public RatGameBuilder(File ratGameFile,
                          Player player,
                          GameActionListener listener) {

    }

    public RatGameBuilder(File ratGameFile,
                          GameActionListener listener) {
        this(ratGameFile, null, listener);
    }

    public RatGame build() {
        //TODO
        //RatGame game = new RatGame();
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

    /**
     * Fetch the currently active player for the game.
     * @return active player.
     */
    private Player loadPlayer() {
        //TODO
        return null;
    }

    private Leaderboard loadLeaderboard() {
        //TODO
        //return new Leaderboard(new ArrayList<Player>());
        return null;
    }


    private ItemGenerator loadGenerator() {
        //TODO
        return null;
    }


}
