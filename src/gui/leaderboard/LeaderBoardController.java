package gui.leaderboard;

import game.level.levels.RatGameLevel;
import game.player.leaderboard.Leaderboard;
import gui.leaderboard.split.LeaderboardModule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Controller for the leaderboard accessed from the main menu. Includes a
 * level selection and a sorted table of the top 10 players.
 *
 * @author Morgan Gardner
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

    public ComboBox leaderboardLevelsComboBox;
    public VBox leaderboardVBox;

    private LeaderboardModule module;
    final HashMap<RatGameLevel, Leaderboard> leaderboards =
            new HashMap<>();

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
     * @param url    Un-used.
     * @param unused Un-used.
     */
    @Override
    public void initialize(final URL url,
                           final ResourceBundle unused) {

        // Load embeddable leaderboard
        module = LeaderboardModule.loadAndGet();

        this.leaderboardVBox.getChildren().add(module.getRoot());

        // Get all level leaderboards
        for (RatGameLevel level : RatGameLevel.values()) {
            leaderboards.put(level, level.getRatGameFile().getLeaderboard());
        }

        for (RatGameLevel level : leaderboards.keySet()) {
            leaderboardLevelsComboBox.getItems().add(
                    level.getRatGameFile().getDefaultProperties()
                            .getLevelName());
        }

        for (Leaderboard leaderboard : leaderboards.values()) {
            module.addAllPlayers(leaderboard.getPlayers());
        }

    }

    /**
     * Filters the leaderboard when a level is chosen.
     *
     * @param actionEvent Unused.
     */
    public void leaderboardLevelSelected(ActionEvent actionEvent) {
        module.removeAllPlayers();
        module.addAllPlayers(leaderboards.get(
                        RatGameLevel.getLevelFromName(
                                leaderboardLevelsComboBox.getValue().toString()))
                .getPlayers());
    }

    /**
     * Set the message of the day to the provided message.
     *
     * @param msg The new message of the day.
     */
    public void setMotdLabel(final String msg) {
        this.motdLabel.setText(msg);
    }
}