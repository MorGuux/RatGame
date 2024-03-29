package gui.game.dependant.endscreen;

import game.RatGameProperties;
import game.event.impl.entity.specific.game.GameEndEvent;
import game.level.reader.RatGameFile;
import game.level.reader.exception.RatGameFileException;
import game.level.writer.RatGameFileWriter;
import game.player.Player;
import game.player.leaderboard.Leaderboard;
import game.tile.exception.UnknownSpriteEnumeration;
import gui.leaderboard.split.LeaderboardModule;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;

/**
 *
 */
public class EndScreenController {

    /**
     * URL Resource of the object hierarchy for the game end screen.
     */
    private static final URL SCENE_FXML
            = EndScreenController.class.getResource("GameEndScene.fxml");

    /**
     * Default winner header text.
     */
    private static final String WINNER_HEADER
            = "Congratulations you are a winner!!!";

    /**
     * Default loser header text.
     */
    private static final String LOSER_HEADER = "Boo you suck!!!";

    /**
     * Leaderboard vbox which should be loaded with a leaderboard.
     */
    @FXML
    private VBox leaderboardVbox;

    /**
     * Title of the scene.
     */
    @FXML
    private Label sceneTitleLabel;

    /**
     * Name of the level.
     */
    @FXML
    private Label levelNameLabel;

    /**
     * Number of columns that the scene has.
     */
    @FXML
    private Label mapWidthLabel;

    /**
     * The height of the game map.
     */
    @FXML
    private Label mapHeightLabel;

    /**
     * The maximum number of rats for the level.
     */
    @FXML
    private Label maxRatsLabel;

    /**
     * The expected clear time for the level.
     */
    @FXML
    private Label expectedClearTimeLabel;

    /**
     * The players name.
     */
    @FXML
    private Label playerNameLabel;

    /**
     * The players clear time.
     */
    @FXML
    private Label playerClearTimeLabel;

    /**
     * The players total score.
     */
    @FXML
    private Label playerTotalScoreLabel;

    /**
     * Loads the scene with the game end screen.
     * @param event The game end event.
     * @return The game end screen.
     */
    public static Parent loadAndWait(final GameEndEvent event) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final EndScreenController controller = loader.getController();

            controller.initFromEvent(event);

            return root;
            // This shouldn't happen and if it does it's out of the callers
            // control.
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Sets up the End screen populating all data fields with the provided
     * end screen data.
     *
     * @param event The event data to display.
     */
    private void initFromEvent(final GameEndEvent event) {
        if (event.isGameWon()) {
            this.sceneTitleLabel.setText(WINNER_HEADER);
        } else {
            this.sceneTitleLabel.setText(LOSER_HEADER);
        }

        final RatGameProperties properties = event.getProperties();
        final Player player = properties.getPlayer();
        final int timeFactor = 1000;

        // Setting level information
        this.expectedClearTimeLabel.setText(String.valueOf(
                properties.getExpectedClearTime() / timeFactor
        ));
        this.maxRatsLabel.setText(String.valueOf(
                properties.getMaxHostileEntities()
        ));
        this.mapWidthLabel.setText(String.valueOf(
                player.getLevel().getColumns()
        ));
        this.mapHeightLabel.setText(String.valueOf(
                player.getLevel().getRows()
        ));

        try {
            final RatGameFile file = player.getLevel().getAsRatGameFile();
            this.levelNameLabel.setText(
                    file.getDefaultProperties().getLevelName()
            );

            // Load embeddable leaderboard
            final LeaderboardModule module
                    = LeaderboardModule.loadAndGet();

            Leaderboard leaderboard =
                    player.getLevel().getAsRatGameFile().getLeaderboard();

            this.leaderboardVbox.getChildren().add(module.getRoot());

            // Add the player to the leaderboard if they won the game.
            if (event.isGameWon()) {
                leaderboard.addPlayer(player);
            }

            module.addAllPlayers(leaderboard.getPlayers());

            //Save the leaderboard to the default file.
            RatGameFileWriter writer = new RatGameFileWriter(file);
            writer.writeModule(
                    RatGameFileWriter.ModuleFormat.LEADERBOARD,
                    leaderboard.buildToString()
            );
            writer.commitToFile();

            // Only an IOException should really occur since we loaded from the
            // RatGameFile anyway
        } catch (UnknownSpriteEnumeration
                | RatGameFileException
                | IOException ex) {
            this.levelNameLabel.setText("Unknown Level!");
        }

        // Setting player information
        this.playerNameLabel.setText(player.getPlayerName());
        this.playerClearTimeLabel.setText(String.valueOf(
                player.getPlayTime() / timeFactor
        ));
        this.playerTotalScoreLabel.setText(String.valueOf(
                player.getCurrentScore()
        ));
    }

    /**
     * Closes the window of the scene that this stage is on.
     */
    @FXML
    private void onContinueClicked() {
        this.sceneTitleLabel.getScene().getWindow().hide();
    }
}
