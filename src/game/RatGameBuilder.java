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

    // todo This has been mostly replaced by the level.reader.RatGameFile I
    //  think this class should still exist but it would just utilise that
    //  object for everything.
    public RatGameBuilder(File ratGameFile,
                          Player player,
                          GameActionListener listener) {

    }

    public RatGameBuilder(File ratGameFile,
                          GameActionListener listener) {
        this(ratGameFile, null, listener);
    }

}
