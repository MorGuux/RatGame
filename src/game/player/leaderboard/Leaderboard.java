package game.player.leaderboard;

import game.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 *
 */
public class Leaderboard {

    //todo setup the leaderboard so that only the top 10 players are kept;
    // excess entries are not collected.
    
    /**
     *
     */
    private ArrayList<LeaderboardEntry> entries = new ArrayList<>();

    /**
     *
     */
    private List<Player> players = new ArrayList<>();

    /**
     *
     */
    private Player activePlayer = null;

    /**
     * @param player
     */
    public void addPlayer(Player player) {
        this.players.add(player);
    }

    /**
     * @return
     */
    public ArrayList<LeaderboardEntry> getEntries() {
        return entries;
    }

    /**
     * @return
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * @return
     */
    public Player getActivePlayer() {
        return activePlayer;
    }

    /**
     * @param activePlayer
     */
    public void setActivePlayer(Player activePlayer) {
        this.activePlayer = activePlayer;
    }

    /**
     * Parses all players currently in the leaderboard into a single string.
     *
     * @return Leaderboard entries separated by new lines.
     */
    public String buildToString() {
        final StringJoiner sj
                = new StringJoiner(System.lineSeparator());
        final String base = "    [%s, %s, %s]";

        for (Player p : players) {
            sj.add(String.format(
                    base,
                    p.getPlayerName(),
                    p.getCurrentScore(),
                    p.getPlayTime()
            ));
        }

        return sj.toString();
    }
}

/**
 *
 */
class LeaderboardEntry {

    /**
     *
     */
    private String name;

    /**
     *
     */
    private int score;

    /**
     * @param name
     * @param score
     */
    public LeaderboardEntry(String name, int score) {
        this.name = name;
        this.score = score;
    }

    /**
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * @return
     */
    public int getScore() {
        return score;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param score
     */
    public void setScore(int score) {
        this.score = score;
    }
}
