package game.player.leaderboard;

import game.player.Player;

import java.util.ArrayList;
import java.util.List;

public class Leaderboard {

    private ArrayList<LeaderboardEntry> entries = new ArrayList<>();
    private List<Player> players = new ArrayList<>();
    private Player activePlayer = null;

    public void addPlayer(Player player) {
        this.players.add(player);
    }

    public ArrayList<LeaderboardEntry> getEntries() {
        return entries;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Player getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(Player activePlayer) {
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
