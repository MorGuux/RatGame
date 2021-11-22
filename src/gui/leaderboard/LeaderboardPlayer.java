package gui.leaderboard;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class LeaderboardPlayer implements Comparable<LeaderboardPlayer> {
    private final SimpleIntegerProperty rank;
    private final SimpleStringProperty name;
    private final SimpleIntegerProperty score;

    public LeaderboardPlayer(final int rank, final String name,
                             final int score) {
        this.rank = new SimpleIntegerProperty(rank);
        this.name = new SimpleStringProperty(name);
        this.score = new SimpleIntegerProperty(score);
    }

    public int getRank() {
        return rank.get();
    }

    public SimpleIntegerProperty rankProperty() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank.set(rank);
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

    @Override
    public int compareTo(LeaderboardPlayer l1) {
        int result;
        if (l1.rank.getValue() > this.rank.getValue()) {
            result = 1;
        } else if (l1.rank.getValue() < this.rank.getValue()) {
            result = -1;
        } else {
            result = 0;
        }
        return result;
    }

}
