package gui.leaderboard;

import game.level.levels.RatGameLevel;
import game.player.leaderboard.Leaderboard;
import gui.leaderboard.split.LeaderboardModule;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
public class LeaderboardController implements Initializable {

    /**
     * Hardcode the Scene Object Hierarchy Resource to the Controller
     * so that it can be accessed.
     */
    public static final URL SCENE_FXML =
            LeaderboardController.class.getResource("Leaderboard.fxml");

    /**
     * The ComboBox that allows the user to select a level.
     */
    @FXML
    private ComboBox<String> leaderboardLevelsComboBox;
    /**
     * The VBox that contains the level selection ComboBox and the embedded
     * leaderboard.
     */
    @FXML
    private VBox leaderboardVBox;

    /**
     * The embedded leaderboard.
     */
    private LeaderboardModule module;

    /**
     * The leaderboards for each level, keyed by the level it belongs to.
     */
    private final HashMap<RatGameLevel, Leaderboard> leaderboards =
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
    public void leaderboardLevelSelected(final ActionEvent actionEvent) {
        module.removeAllPlayers();
        module.addAllPlayers(leaderboards.get(
                        RatGameLevel.getLevelFromName(
                                leaderboardLevelsComboBox.getValue()))
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
