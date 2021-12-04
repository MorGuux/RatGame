package gui.game.dependant.endscreen;

import game.RatGameProperties;
import game.event.impl.entity.specific.game.GameEndEvent;
import game.level.reader.RatGameFile;
import game.level.reader.exception.RatGameFileException;
import game.player.Player;
import game.tile.exception.UnknownSpriteEnumeration;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;

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
     *
     */
    private static final String WINNER_HEADER
            = "Congratulations you are a winner!!!";

    /**
     *
     */
    private static final String LOSER_HEADER = "Boo you suck!!!";

    /**
     *
     */
    @FXML
    private Label sceneTitleLabel;

    /**
     *
     */
    @FXML
    private Label levelNameLabel;

    /**
     *
     */
    @FXML
    private Label mapWidthLabel;

    /**
     *
     */
    @FXML
    private Label mapHeightLabel;

    /**
     *
     */
    @FXML
    private Label maxRatsLabel;

    /**
     *
     */
    @FXML
    private Label expectedClearTimeLabel;

    /**
     *
     */
    @FXML
    private Label playerNameLabel;

    /**
     *
     */
    @FXML
    private Label playerClearTimeLabel;

    /**
     *
     */
    @FXML
    private Label playerTotalScoreLabel;

    /**
     *
     */
    @FXML
    private ScrollPane levelLeaderboardScrollPane;

    /**
     * @param event The game end event
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
