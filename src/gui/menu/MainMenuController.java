package gui.menu;

import game.level.levels.RatGameLevel;
import game.level.reader.RatGameFile;
import game.motd.MOTDClient;
import game.player.Player;
import gui.game.GameController;
import gui.menu.dependant.level.LevelInputForm;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import launcher.Main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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
public class MainMenuController implements Initializable {

    /**
     * Hardcode the Scene Object Hierarchy Resource to the Controller
     * so that it can be accessed.
     */
    public static final URL SCENE_FXML =
            MainMenuController.class.getResource("MainMenu.fxml");

    /**
     * Message of the day client; our webhook to CS-Webcat.
     */
    private MOTDClient client;

    /**
     * Timer that constantly checks for new messages.
     */
    private Timer motdPinger;

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
     * @param url    Un-used.
     * @param unused Un-used.
     */
    @Override
    public void initialize(final URL url,
                           final ResourceBundle unused) {
        client = new MOTDClient();
        motdPinger = new Timer();
        startMotdTracker();

        System.out.println("Initialised called!");
    }

    /**
     * Updates the Message of the Day label to a new message.
     *
     * @param msg The new message of the day.
     */
    private void handleNewMessage(final String msg) {
        Platform.runLater(() -> motdLabel.setText(msg));
    }

    /**
     *
     */
    private void startMotdTracker() {
        final int delay = 5_000;
        motdPinger.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (client.hasNewMessage()) {
                    try {
                        final String message = client.getMessage();
                        Platform.runLater(() -> motdLabel.setText(message));
                        // Should never happen
                    } catch (IOException
                            | InterruptedException e) {
                        e.printStackTrace();
                        System.exit(-1);
                    }
                }
            }
        }, 0, delay);
    }

    /**
     * Disables the Message of the Day tracker.
     */
    private void stopMotdTracker() {
        this.motdPinger.cancel();
    }

    /**
     * Initialises a brand new RatGame. You will need to gather the
     * following:
     * <ol>
     *     <li>The Rat Game Level (Default)</li>
     *     <li>Player Profile</li>
     * </ol>
     */
    public void onStartGameClicked() throws Exception {

        this.backgroundPane.getScene().getWindow().hide();
        final LevelInputForm form = LevelInputForm.loadAndWait(
                new Stage(),
                RatGameLevel.values()
        );

        final Optional<String> name = form.getPlayerName();
        final Optional<RatGameFile> level = form.getLevelSelection();

        final GameController gameScene = GameController.loadAndGet(
                new Player(name.orElse("Unknown Player")),
                level.orElse(RatGameLevel.LEVEL_ONE.getRatGameFile())
        );

        gameScene.startGame(new Stage());

        this.motdLabel.getScene().getWindow().hide();
        stopMotdTracker();
    }

    /**
     *
     */
    public void onLoadGameClicked() {
        // todo

        // < Actual Implementation >
        //  > Load Player Save File
    }

    /**
     *
     */
    public void onAboutClicked() {
        //todo

        // < Actual Implementation >
        //  > Display text window containing
        //      > Group Member
        //      > Course
        //      > Lecturer
        //      > Original Game Author
    }

    /**
     * Updates the Applications global stylesheet which all scenes utilise.
     */
    public void onChangeStyleClicked() {
        try {
            final String theme = Main.cycleCssTheme();
            Main.setStyleSheet(theme);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the leaderboard
     */
    public void onShowLeaderboardClicked() throws IOException {
        final FXMLLoader loader = Main.loadLeaderboardStage();
        final Scene sc = new Scene(loader.load());
        Main.loadNewScene(sc);
    }
}
