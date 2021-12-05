package game.player.leaderboard;

import game.player.Player;
import gui.leaderboard.split.LeaderboardModule;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.StringJoiner;

/**
 * Leaderboard class that stores the top 10 players of a level.
 *
 * @author Morgan Gardner
 * @version 0.2
 * Copyright: N/A
 */
public class Leaderboard {

    /**
     * The list of players in the leaderboard.
     */
    private final ArrayList<Player> players = new ArrayList<>();

    /**
     * The maximum number of players to record on a leaderboard.
     */
    private static final int MAX_PLAYERS = 10;

    /**
     * Adds a player to the leaderboard.
     *
     * @param player The player to add to the leaderboard.
     */
    public void addPlayer(Player player) {

        /* Check if player is already in the leaderboard, if so, update their
        score if it is higher than what is present. */
        final Player existingPlayer = this.players.stream()
                .filter(p -> p.getPlayerName().equals(player.getPlayerName()))
                .findFirst()
                .orElse(null);

        if (existingPlayer != null) {
            if (existingPlayer.getCurrentScore() < player.getCurrentScore()) {
                existingPlayer.setCurrentScore(player.getCurrentScore());
            }
        } else {
            /*If number of players equals max players, remove the player with
            the lowest score to be able to add the new one (if the new
            player's score is higher). */
            if (this.players.size() == MAX_PLAYERS) {
                final Player lowestScorePlayer = this.getLowestScorePlayer();
                if (player.getCurrentScore() > lowestScorePlayer
                        .getCurrentScore()) {
                    this.players.remove(lowestScorePlayer);
                }
            }
            this.players.add(player);
        }
    }

    /**
     * Gets the player with the lowest score.
     *
     * @return The player with the lowest score.
     */
    private Player getLowestScorePlayer() {
        players.sort(Comparator.comparingInt(Player::getCurrentScore));
        return this.players.get(this.players.size() - 1);
    }

    /**
     * Gets the list of players in the leaderboard, sorted by score descending.
     *
     * @return The list of players in the leaderboard.
     */
    public List<Player> getPlayers() {
        players.sort(Comparator.comparingInt(Player::getCurrentScore));
        return players;
    }

    /**
     * Parses all players currently in the leaderboard into a single string;
     * Order of the entries is best on lowest score to the highest score.
     *
     * @return Leaderboard entries separated by new lines.
     */
    public String buildToString() {
        players.sort(Comparator.comparingInt(Player::getCurrentScore));
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