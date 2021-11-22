package game;

import game.entity.Entity;
import game.event.GameActionListener;
import game.player.Player;
import jdk.jshell.spi.ExecutionControl;

import java.io.File;
import java.util.HashMap;
import java.util.List;

public class RatGameBuilder {

    public RatGameBuilder(File ratGameFile, Player player,
                          GameActionListener listener) {

    }

    public RatGameBuilder(File ratGameFile, GameActionListener listener) {

    }

    public RatGame build() {
        //TODO
        return null;
    }

    private HashMap<Entity, List<Integer[]>> loadEntities (){
        //TODO
        return null;
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
