package game.player.leaderboard;

import java.util.ArrayList;

public class Leaderboard {

    private ArrayList<LeaderboardEntry> players;
    private final int targetLevel;

    /**
     * Create a new leaderboard with a given target level. The entries will
     * be read from the associated level file.
     * @param targetLevel the target level
     */
    public Leaderboard(int targetLevel) {
        this.targetLevel = targetLevel;
    }

}

class LeaderboardEntry {

    private String name;
    private int score;

    public LeaderboardEntry(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
