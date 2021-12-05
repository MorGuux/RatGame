package gui.leaderboard;

import game.player.Player;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class LeaderboardPlayer implements Comparable<LeaderboardPlayer> {
    private final SimpleStringProperty name;
    private final SimpleIntegerProperty score;
    private String level = "Unknown Level";

    public LeaderboardPlayer(final Player player) {
        this.name = new SimpleStringProperty(player.getPlayerName());
        this.score = new SimpleIntegerProperty(player.getCurrentScore());
        try {
            this.level = player
                    .getLevel()
                    .getAsRatGameFile()
                    .getDefaultProperties()
                    .getLevelName();
        } catch (Exception ex) {

        }

    }

    public String getLevel() {
        return level;
    }

    public String getName() {
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public int getScore() {
        return score.get();
    }

    public SimpleIntegerProperty scoreProperty() {
        return score;
    }

    public void setScore(int score) {
        this.score.set(score);
    }

    /**
     * Compares two players by their score.
     * @param l1 the first player
     * @return -1 if the first player has a lower score, 0 if they have the same
     * score, 1 if the first player has a higher score.
     */
    @Override
    public int compareTo(LeaderboardPlayer l1) {
        return l1.score.getValue().compareTo(this.score.getValue());
    }

}
