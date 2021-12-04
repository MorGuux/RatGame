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
        private final Player player;

        EmbeddablePlayer(final Player p) {
            this.player = p;
        }

        public String getPlayerName() {
            return this.player.getPlayerName();
        }

        public int getPlayerScore() {
            return this.player.getCurrentScore();
        }
    }

    @FXML
    private BorderPane root;

    /**
     *
     */
    @FXML
    private TableView<EmbeddablePlayer> tableView;

    /**
     *
     */
    @FXML
    private TableColumn<?, ?> playerNameColumn;

    /**
     *
     */
    @FXML
    private TableColumn<?, ?> totalScoreColumn;

    private final ObservableList<EmbeddablePlayer> players =
            FXCollections.observableArrayList();

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
     * @param players The players to add to the leaderboard.
     */
    public void addAllPlayers(final List<Player> players) {
        // Load them into the scene
        players.forEach(i ->
                this.players.add(new EmbeddablePlayer(i))
        );
    }

    /**
     * Adds a single player to the leaderboard.
     * @param player The player to add to the leaderboard.
     */
    public void addPlayer(final Player player) {
        this.players.add(new EmbeddablePlayer(player));
    }
}
