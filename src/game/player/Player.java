package game.player;

import java.util.ArrayList;

/**
 * Player Class represents a person playing the Rat game the player could
 * have existed before, or not, doesn't matter.
 *
 * @author Maksim
 * @version 0.1
 * Copyright: N/A
 */
public class Player {

    /**
     * The players name. This is a unique field that cannot change ever, no
     * duplicates should be allowed either.
     */
    private final String playerName;

    /**
     * Time elapsed in the current level in milliseconds.
     */
    private int timePlayed;

    /**
     * Score value in the current level.
     */
    private int currentScore;

    /**
     * All levels this player has unlocked.
     */
    private final ArrayList<Object> levelsUnlocked;

    /**
     * Level this player is currently playing.
     */
    private Object level;

    /**
     * Constructs a new player instance with a name, list of levels unlocked
     * and the current level in progress ( if applicable).
     *
     * @param playerName - Name of the player, a string.
     */
    public Player(final String playerName) {
        this.playerName = playerName;
        this.levelsUnlocked = null; //sorry will figure out later
        this.level = null;          // i promise
        // (in question)
        this.timePlayed = 0;
        this.currentScore = 0;
    }

    /**
     * Constructor used to load an  instance of a player from a save file.
     * and the current currentLevel in progress ( if applicable)
     *
     * @param playerName     - Name of the player, a string
     * @param timePlayed     - long storing the current timer of the level
     * @param currentScore   - int storing current score for the level
     * @param levelsUnlocked - array list of levels unlocked for the player
     * @param currentLevel   - the current currentLevel player is on
     */
    public Player(final String playerName,
                  final int timePlayed,
                  final int currentScore,
                  final ArrayList<Object> levelsUnlocked,
                  final Object currentLevel) {
        this.playerName = playerName;
        this.timePlayed = timePlayed;
        this.currentScore = currentScore;
        this.levelsUnlocked = levelsUnlocked;
        this.level = currentLevel;
        // (in question).
    }

    /**
     * @return This player's name.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * @return Time elapsed in the current game for this player.
     */
    public int getTimePlayed() {
        return timePlayed;
    }

    /**
     * @return Current score of the player.
     */
    public int getCurrentScore() {
        return currentScore;
    }

    /**
     * @return All unlocked levels available to a player.
     */
    public ArrayList<Object> getLevelsUnlocked() {
        return levelsUnlocked;
    }

    /**
     * @return Current level this player is playing.
     */
    public Object getLevel() {
        return level;
    }

    /**
     * Set the time played for this player.
     *
     * @param newTimePlayed The new time played.
     */
    public void setTimePlayed(final int newTimePlayed) {
        this.timePlayed = newTimePlayed;
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
    public void setLevel(final Object newLevel) {
        this.level = newLevel;
    }
}
