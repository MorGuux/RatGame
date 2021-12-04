package game.player;

import game.level.Level;
import game.level.levels.RatGameLevel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Player Class represents a person playing the Rat game the player could
 * have existed before, or not, doesn't matter.
 *
 * @author Maksim
 * @version 0.3
 * Copyright: N/A
 */
public class Player {

    /**
     * The players name. This is a unique field that cannot change ever, no
     * duplicates should be allowed either.
     */
    private final String playerName;

    /**
     * Score value in the current level.
     */
    private int currentScore;

    /**
     * How long has the player been playing the level in milliseconds.
     */
    private int playTime;

    /**
     * Paths to the levels this player has unlocked.
     */
    private final List<RatGameLevel> levelsUnlocked;

    /**
     * Level this player is currently playing.
     */
    private Level level;

    /**
     * Constructs a new player instance with a name, list of levels unlocked
     * and the current level in progress ( if applicable).
     *
     * @param playerName - Name of the player, a string.
     */
    public Player(final String playerName) {
        this.playerName = playerName;
        this.levelsUnlocked = new ArrayList<>();
        this.levelsUnlocked.add(RatGameLevel.LEVEL_ONE);
        this.currentScore = 0;
        this.playTime = 0;
    }

    /**
     * Constructs a player from a name and all the known levels that they have
     * unlocked.
     *
     * @param playerName     The name of the player.
     * @param levelsUnlocked The levels that the player has unlocked.
     */
    public Player(final String playerName,
                  final List<RatGameLevel> levelsUnlocked) {
        this.playerName = playerName;
        this.levelsUnlocked = levelsUnlocked;
        this.currentScore = 0;
        this.playTime = 0;
    }

    /**
     * Constructor used to load an  instance of a player from a save file.
     * and the current currentLevel in progress ( if applicable)
     *
     * @param playerName   Name of the player, a string
     * @param currentScore int storing current score for the level
     * @param currentLevel the current currentLevel player is on
     */
    public Player(final String playerName,
                  final int currentScore,
                  final int playTime,
                  final List<RatGameLevel> levelsUnlocked,
                  final Level currentLevel) {
        this.playerName = playerName;
        this.currentScore = currentScore;
        this.playTime = playTime;
        this.levelsUnlocked = levelsUnlocked;
        this.level = currentLevel;
    }

    /**
     * @return This player's name.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @return Current score of the player.
     */
    public int getCurrentScore() {
        return currentScore;
    }

    /**
     * @return All unlocked levels available to a player represented as Paths
     * to the level files.
     */
    public RatGameLevel[] getLevelsUnlocked() {
        return levelsUnlocked.toArray(new RatGameLevel[0]);
    }

    /**
     * @return Current level this player is playing.
     */
    public Level getLevel() {
        return level;
    }

    /**
     * Set the current score of the player.
     *
     * @param newCurrentScore The new score for the player.
     */
    public void setCurrentScore(final int newCurrentScore) {
        this.currentScore = newCurrentScore;
    }

    /**
     * Set the current level this player is playing.
     *
     * @param newLevel The level the player is playing.
     */
    public void setLevel(final Level newLevel) {
        this.level = newLevel;
    }

    /**
     * @return The length in milliseconds that the player has been playing for.
     */
    public int getPlayTime() {
        return playTime;
    }

    /**
     * @param playTime Set the time that the player has been playing.
     */
    public void setPlayTime(final int playTime) {
        this.playTime = playTime;
    }

    /**
     * Checks to see if the provided players equals this player. Where
     * equality is based on a Case sensitive match for the player name.
     *
     * @param obj The object to test for equality.
     * @return {@code true} if the object is a player and the name of player
     * is the same as this player.
     */
    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Player) {
            return ((Player) obj).getPlayerName().equals(this.playerName);
        } else {
            return false;
        }
    }
}
