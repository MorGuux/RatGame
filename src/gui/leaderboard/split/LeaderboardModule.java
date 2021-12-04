package gui.leaderboard.split;

import game.player.Player;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
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

        public int getClearTime() {
            final int timeScale = 1000;
            return this.player.getPlayTime() / timeScale;
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
    private TableColumn<?, ?> playerClearTimeColumn;

    /**
     *
     */
    @FXML
    private TableColumn<?, ?> totalScoreColumn;

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
        this.playerClearTimeColumn.setCellValueFactory(
                new PropertyValueFactory<>("clearTime")
        );
        this.totalScoreColumn.setCellValueFactory(new PropertyValueFactory<>(
                "playerScore"
        ));

        // Force the size of the columns to fit the width of the table view
        final int numCols = 3;
        tableView.getColumns().forEach(i -> {
            i.prefWidthProperty().bind(
                    tableView.widthProperty().divide(numCols)
            );
        });
    }

    /**
     * Adds all the players to the leaderboard.
     *
     * @param players The players to add to the leaderboard.
     */
    public void addAllPlayers(final List<Player> players) {
        // Load them into the scene
        players.forEach(i ->
                this.tableView.getItems().add(new EmbeddablePlayer(i))
        );
    }
}
