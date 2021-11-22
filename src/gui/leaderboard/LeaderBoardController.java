package gui.leaderboard;

import game.motd.MOTDClient;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import launcher.Main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Main menu scene controller.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class LeaderBoardController implements Initializable {

    /**
     * Hardcode the Scene Object Hierarchy Resource to the Controller
     * so that it can be accessed.
     */
    public static final URL SCENE_FXML =
            LeaderBoardController.class.getResource("LeaderBoard.fxml");

    @FXML
    private TableView leaderboardTableView;

    /**
     * Pane which sits behind the main menu buttons.
     */
    @FXML
    private Pane backgroundPane;

    /**
     * Message of the day label.
     */
    @FXML
    private Label motdLabel;

    /**
     * Setup MOTD pinger to constantly update the new
     * message of the day.
     *
     * @param url Un-used.
     * @param unused Un-used.
     */
    @Override
    public void initialize(final URL url,
                           final ResourceBundle unused) {
        //TODO
        var column1 = (TableColumn)leaderboardTableView.getColumns().get(0);
        column1.setCellValueFactory(new PropertyValueFactory<>("rank"));
        var column2 = (TableColumn)leaderboardTableView.getColumns().get(1);
        column2.setCellValueFactory(new PropertyValueFactory<>("name"));
        var column3 = (TableColumn)leaderboardTableView.getColumns().get(2);
        column3.setCellValueFactory(new PropertyValueFactory<>("score"));

        leaderboardTableView.getItems().add(new LeaderboardPlayer(1, "Morgan",
                1000));
    }

}

class LeaderboardPlayer {
    public SimpleIntegerProperty rank;
    public SimpleStringProperty name;
    public SimpleIntegerProperty score;

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
}