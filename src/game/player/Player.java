package game.player;

import java.util.ArrayList;

public class Player {

    /**
     * @param playerName - Name of the player, a string
     * @param timePlayed - the current timer value for the current level. 0
     *                   if no current level exists. Haven't started yet.
     * @param levelsUnlocked - array list of levels unlocked for the player
     * @param level - the current level player is on
     */

    private String playerName;
    private long timePlayed;
    private int currentScore;
    private ArrayList<Object> levelsUnlocked;
    private Object level;

    /**
     * Constructs a new player instance with a name, list of levels unlocked
     * and the current level in progress ( if applicable).
     *
     * @param playerName - Name of the player, a string.
     */
    void Player (String playerName) {
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
     * @param playerName - Name of the player, a string
     * @param timePlayed - long storing the current timer of the level
     * @param currentScore - int storing current score for the level
     * @param levelsUnlocked - array list of levels unlocked for the player
     * @param currentLevel - the current currentLevel player is on
     */

    void Player (String playerName,
                 long timePlayed,
                 int currentScore, ArrayList<Object> levelsUnlocked,
                 Object currentLevel) {
        this.playerName = playerName;
        this.timePlayed = 0;
        this.currentScore = 0;
        this.levelsUnlocked = levelsUnlocked;
        this.level = currentLevel;
        // (in question).
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setTimePlayed(long timePlayed) {
        this.timePlayed = timePlayed;
        //TODO : Implement update method for this class.
    }

    public void setCurrentScore(int currentScore) {
        this.currentScore = currentScore;
        //TODO : Implement update method for this class.
    }

    public void setLevelsUnlocked(ArrayList<Object> levelsUnlocked) {
        this.levelsUnlocked = levelsUnlocked;
    }

    public void setLevel(Object level) {
        this.level = level;
    }


    public String getPlayerName() {
        return playerName;
    }

    public long getTimePlayed() {
        return timePlayed;
    }

    public int getCurrentScore() {
        return currentScore;
    }

    public ArrayList<Object> getLevelsUnlocked() {
        return levelsUnlocked;
    }

    public Object getLevel() {
        return level;
    }

}
