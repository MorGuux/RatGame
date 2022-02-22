package gui.leaderboard.split;

import game.player.Player;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

/**
 * Abstract leaderboard scene that can be embedded into a stage/scene.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class LeaderboardModule {

    /**
     * Main scene fxml resource.
     */
    private static final URL FXML_SCENE =
            LeaderboardModule.class.getResource("LeaderboardSceneEdit.fxml");

    /**
     * Inner wrapper class for the tableview.
     *
     * @author -Ry
     * @version 0.1
     * Copyright: N/A
     */
    public static class EmbeddablePlayer {
        /**
         * Player reference from leaderboard.
         */
        private final Player player;

        /**
         * Constructor of the embeddable player. Sets the reference to the
         * player.
         * @param p
         */
        EmbeddablePlayer(final Player p) {
            this.player = p;
        }

        /**
         * Gets the player name.
         * @return The name of the player.
         */
        public String getPlayerName() {
            return this.player.getPlayerName();
        }

        /**
         * Gets the player score.
         * @return The score of the player.
         */
        public int getPlayerScore() {
            return this.player.getCurrentScore();
        }
    }

    /**
     * The root node of the scene.
     */
    @FXML
    private BorderPane root;

    /**
     * Tableview for the leaderboard.
     */
    @FXML
    private TableView<EmbeddablePlayer> tableView;

    /**
     * Tableview column for the player name.
     */
    @FXML
    private TableColumn<?, ?> playerNameColumn;

    /**
     * Tableview column for the player score.
     */
    @FXML
    private TableColumn<?, ?> totalScoreColumn;

    /**
     * The list of players to display.
     */
    private final ObservableList<EmbeddablePlayer> players =
            FXCollections.observableArrayList();

    /**
     * The sorted list of players, bound to the players list.
     */
    private final SortedList<EmbeddablePlayer> sortedPlayers =
            new SortedList<>(players,
                    Comparator.comparingInt(EmbeddablePlayer::getPlayerScore)
                            .reversed());

    /**
     * Initialises the scene ready to be embedded into a scene.
     *
     * @return Instantiated controller.
     */
    public static LeaderboardModule loadAndGet() {
        final FXMLLoader loader = new FXMLLoader(FXML_SCENE);

        try {
            loader.load();

            final LeaderboardModule mod = loader.getController();
            mod.initScene();

            return mod;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * @return The parent of the leaderboard scene which can be embedded into
     * a scene.
     */
    public Parent getRoot() {
        return this.root;
    }

    public BorderPane getRootPane() {
        return this.root;
    }

    /**
     * @return The table view of the module.
     */
    public TableView<EmbeddablePlayer> getTableView() {
        return tableView;
    }

    /**
     * Bind the property values for the cells to their associated value name.
     */
    private void initScene() {
        this.playerNameColumn.setCellValueFactory(new PropertyValueFactory<>(
                "playerName"
        ));
        this.totalScoreColumn.setCellValueFactory(new PropertyValueFactory<>(
                "playerScore"
        ));

        this.tableView.setItems(this.sortedPlayers);
    }

    /**
     * Adds all the players to the leaderboard.
     *
     * @param playersToAdd The players to add to the leaderboard.
     */
    public void addAllPlayers(final List<Player> playersToAdd) {
        // Load them into the scene
        playersToAdd.forEach(p ->
                this.players.add(new EmbeddablePlayer(p))
        );
    }

    /**
     * Removes all the players from the leaderboard.
     */
    public void removeAllPlayers() {
        this.players.clear();
    }

    /**
     * Gets a single player from the leaderboard, by their name.
     *
     * @param playerName The name of the player to get.
     * @return The player with the given name.
     */
    public EmbeddablePlayer getPlayer(final String playerName) {
        for (EmbeddablePlayer player : this.players) {
            if (player.getPlayerName().equals(playerName)) {
                return player;
            }
        }
        return null;
    }
}
