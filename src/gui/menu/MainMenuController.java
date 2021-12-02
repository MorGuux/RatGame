package gui.menu;

import game.level.levels.RatGameLevel;
import game.level.reader.RatGameFile;
import game.level.reader.RatGameSaveFile;
import game.level.reader.exception.RatGameFileException;
import game.motd.MOTDClient;
import game.player.Player;
import game.tile.exception.UnknownSpriteEnumeration;
import gui.game.GameController;
import gui.menu.dependant.level.LevelInputForm;
import gui.menu.dependant.save.SaveSelectionController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import launcher.Main;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * Main menu scene controller.
 *
 * @author -Ry
 * @version 0.3
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
     * The time in milliseconds that the MOTD Client will be checked by the
     * motdPinger for new messages.
     */
    private static final int UPDATE_RATE = 5000;

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
     * A list of the motd pingers that will be notified every 5 seconds about
     * a message of the day. Synchronised so that we don't have to stop the
     * motdPinger in order to register a new pinger.
     */
    private final List<Consumer<String>> motdPingers
            = Collections.synchronizedList(new ArrayList<>());

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
        motdPingers.add((s) -> this.motdLabel.setText(s));

        motdPinger.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String msg = client.getMessage();
                if (client.hasNewMessage()) {
                    msg = client.getMessage();
                }

                // Looks a bit weird, but we always want to set the string on
                // an update
                final String actual = msg;
                for (Consumer<String> pinger : motdPingers) {
                    Platform.runLater(() -> {
                        pinger.accept(actual);
                    });
                }

            }
        }, 0, UPDATE_RATE);

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

        this.motdPingers.add(gameScene::setMotdText);
        gameScene.startGame(new Stage());

        this.motdLabel.getScene().getWindow().hide();
        stopMotdTracker();
    }

    /**
     *
     */
    public void onLoadGameClicked() {
        final TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Please type a player name!");
        final Optional<String> name = dialog.showAndWait();

        if (name.isPresent()) {
            final String username = name.get();

            try {
                // Get all save files
                final DirectoryStream<Path> files = Files.newDirectoryStream(
                        Path.of(RatGameLevel.SAVES_DIR),
                        entry -> entry.toString().endsWith(".rgs")
                );

                // Parse all valid save files
                final List<RatGameSaveFile> saves = new ArrayList<>();
                files.forEach(i -> {
                    try {
                        saves.add(new RatGameSaveFile(i.toFile()));
                    } catch (IOException
                            | RatGameFileException
                            | UnknownSpriteEnumeration ex) {
                        final Alert e = new Alert(Alert.AlertType.ERROR);
                        e.setHeaderText("Save File Invalid!");
                        e.setContentText(i.toString());
                        e.showAndWait();
                    }
                });

                // Only those saves with the correct username need to be shown
                saves.removeIf(i ->
                        !i.getPlayer().getPlayerName().equals(username)
                );

                // Prompt for level selection
                final SaveSelectionController e =
                        SaveSelectionController.loadAndGet(
                                saves
                        );
                final Stage s = new Stage();
                s.setScene(new Scene(e.getRoot()));
                s.initModality(Modality.APPLICATION_MODAL);
                s.showAndWait();

                // Compute result from selection
                final Optional<RatGameSaveFile> save = e.getSelection();
                save.ifPresent(this::createGame);

            } catch (IOException ex) {
                final Alert e = new Alert(Alert.AlertType.ERROR);
                e.setHeaderText("Unexpected issue encountered!");
                e.showAndWait();
            }
        }
    }

    /**
     * Creates a Rat game from a save file.
     *
     * @param save File to load a game from.
     */
    private void createGame(final RatGameSaveFile save) {
        try {
            final GameController gameScene = GameController.loadAndGet(
                    save.getPlayer(),
                    save
            );

            this.motdPingers.add(gameScene::setMotdText);
            gameScene.startGame(new Stage());

        } catch (IOException e) {
            final Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Failed to load save!");
            alert.setContentText(save.getDefaultFile());
            alert.showAndWait();
        }
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
