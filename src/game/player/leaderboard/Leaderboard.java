package game.player.leaderboard;

import game.player.Player;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard {

    private ArrayList<LeaderboardEntry> entries;
    private List<Player> players;
    private Player activePlayer;

    /**
     * Create a new leaderboard with a given target level. The entries will
     * be read from the associated level file.
     * @param players The players to be added to the leaderboard.
     */
    public Leaderboard(List<Player> players, Player activePlayer) {
        this.players = players;
        this.activePlayer = activePlayer;
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
