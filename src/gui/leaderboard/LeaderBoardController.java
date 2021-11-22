package gui.leaderboard;

import game.motd.MOTDClient;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import launcher.Main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Predicate;

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
    public TableColumn rankColumn;
    public ComboBox leaderboardLevelsComboBox;

    @FXML
    private TableView<LeaderboardPlayer> leaderboardTableView;

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
     * The list of players to display in the leaderboard.
     */
    private static final ObservableList<LeaderboardPlayer>
            leaderboardPlayersObservableList =
            FXCollections.observableArrayList();

    /**
     * A filtered list of players in the leaderboard, filtered by the chosen
     * level.
     */
    private static final FilteredList<LeaderboardPlayer> filteredData =
            new FilteredList<>(leaderboardPlayersObservableList,
                    p -> true);

    /**
     * A sorted list of players in the filtered leaderboard, sorted by rank.
     */
    private static final SortedList<LeaderboardPlayer> sortedList =
            new SortedList<>(filteredData);

    /**
     * @param url Un-used.
     * @param unused Un-used.
     */
    @Override
    public void initialize(final URL url,
                           final ResourceBundle unused) {
        //TODO
        leaderboardLevelsComboBox.getItems().add(1);
        leaderboardLevelsComboBox.getItems().add(2);
        leaderboardLevelsComboBox.getItems().add(3);


        //TODO Temporary tests
        leaderboardPlayersObservableList.add(new LeaderboardPlayer(3, "Bob",
                200, 1));
        leaderboardPlayersObservableList.add(new LeaderboardPlayer(2, "Dave",
                300, 1));
        leaderboardPlayersObservableList.add(new LeaderboardPlayer(1, "Steve",
                400, 1));
        leaderboardPlayersObservableList.add(new LeaderboardPlayer(1, "John",
                700, 2));
        leaderboardPlayersObservableList.add(new LeaderboardPlayer(2, "Jane",
                500,3));

        leaderboardTableView.setItems(sortedList);
        sortedList.comparatorProperty().bind(leaderboardTableView.comparatorProperty());

        leaderboardTableView.getSortOrder().add(rankColumn);

    }

    /**
     * Predicate for filtering the leaderboard.
     * @return Predicate.
     */
    private Predicate<LeaderboardPlayer> leaderboardPlayerPredicate() {
        try {
            return p -> p.getLevel() == (int)leaderboardLevelsComboBox.getValue();
        } catch (Exception ex) {
            return p -> true;
        }
    }

    /**
     * Filters the leaderboard when a level is chosen.
     * @param actionEvent Unused.
     */
    public void leaderboardLevelSelected(ActionEvent actionEvent) {
        filteredData.setPredicate(leaderboardPlayerPredicate());
    }
}