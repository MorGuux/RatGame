package gui.menu;

import game.event.impl.entity.specific.game.GameEndEvent;
import game.level.levels.RatGameLevel;
import game.level.levels.players.PlayerDataBase;
import game.level.reader.RatGameFile;
import game.level.reader.RatGameSaveFile;
import game.level.reader.exception.InvalidArgsContent;
import game.level.reader.exception.RatGameFileException;
import game.motd.MOTDClient;
import game.player.Player;
import game.tile.exception.UnknownSpriteEnumeration;
import gui.game.GameController;
import gui.leaderboard.LeaderboardController;
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
import java.net.URISyntaxException;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Main menu scene controller.
 *
 * @author -Ry
 * @version 0.5
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
     * Regex wraps a bunch of characters that a player cannot have as their
     * name or in their name.
     */
    private static final String ILLEGAL_CHARS_REGEX
            = "[_\\[\\];:'@#~<,>.?/\\\\!\"£$%^&*(){}]";

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
     * Database of all known players.
     */
    private PlayerDataBase dataBase;

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

        // Creating message of the day client
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
                // an update.
                final String actual = msg;
                for (Consumer<String> pinger : motdPingers) {
                    Platform.runLater(() -> {
                        pinger.accept(actual);
                    });
                }

            }
        }, 0, UPDATE_RATE);


        // Shutdown the timer task when the scene is closed (has to be
        // initialised after load)
        Platform.runLater(() -> {
            this.backgroundPane.getScene().getWindow().setOnCloseRequest(
                    (e) -> this.motdPinger.cancel()
            );
        });

        try {
            this.dataBase = new PlayerDataBase();

            // Don't proceed if the player database could not be loaded.
        } catch (IOException | URISyntaxException | InvalidArgsContent e) {
            final Alert ae = new Alert(Alert.AlertType.ERROR);
            ae.setHeaderText("Fatal Exception Occurred!");
            ae.setContentText("Program cannot continue as vital dependencies "
                    + "failed to load.");
            ae.showAndWait();
            System.exit(-1);
        }
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

        final TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Type in a username!");
        final Optional<String> name = dialog.showAndWait();

        // Show levels they've unlocked
        if (name.isPresent() && isSafeName(name.get())) {

            if (dataBase.isPlayerPresent(name.get())) {
                final Player p = dataBase.getPlayer(name.get());

                // Get level selection and start game if present
                final Optional<RatGameFile> level =
                        getLevelSelectionForPlayer(p);
                level.ifPresent((lvl) -> initGameFor(p, lvl, dataBase));

                // Player should be created
            } else {
                final Player p = new Player(name.get());

                // Get selection and init game if present
                final Optional<RatGameFile> level
                        = getLevelSelectionForPlayer(p);
                level.ifPresent((lvl) -> initGameFor(p, lvl, dataBase));
            }
        }
    }

    /**
     * Ensure that the provided name is not a bunch of whitespace and also
     * does not contain any illegal/special characters.
     *
     * @param name The name to validate.
     * @return {@code true} if the provided name is a valid and safe name.
     * Otherwise, {@code false} is returned.
     */
    private boolean isSafeName(final String name) {
        if (name.matches("\\s*")) {
            return false;
        }

        final Matcher m
                = Pattern.compile(ILLEGAL_CHARS_REGEX).matcher(name);

        return !m.find();
    }

    /**
     * Loads a game up for the target player on the target level.
     *
     * @param p   The player who is playing.
     * @param lvl The level they are playing.
     */
    private void initGameFor(final Player p,
                             final RatGameFile lvl,
                             final PlayerDataBase dataBase) {
        try {
            final GameController game
                    = GameController.loadAndGet(p, lvl);

            // Add the message of the day pinger to ping this
            final Consumer<String> motdPinger = game::setMotdText;
            this.motdPingers.add(motdPinger);

            // Start game (waits until finished)
            final Stage s = new Stage();
            s.initModality(Modality.APPLICATION_MODAL);
            game.startGame(s);

            final Optional<GameEndEvent> gameState = game.getGameResult();

            // If player has won mark this level as completed
            if (gameState.isPresent() && gameState.get().isGameWon()) {
                final String idName
                        = lvl.getDefaultProperties().getIdentifierName();
                p.setLevelCompleted(RatGameLevel.getLevelFromName(idName));
                dataBase.commitPlayer(p);
            }

            // No longer needed in the game scene
            this.motdPingers.remove(motdPinger);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Prompts the user about a level selection for the levels that they have
     * unlocked.
     *
     * @param p The player who we're prompting.
     * @return The level they have selected.
     */
    private Optional<RatGameFile> getLevelSelectionForPlayer(final Player p) {
        try {
            final LevelInputForm form = LevelInputForm.loadAndWait(
                    new Stage(),
                    p.getLevelsUnlocked()
            );

            return form.getLevelSelection();

            // Error case
        } catch (Exception e) {
            final Alert ae = new Alert(Alert.AlertType.ERROR);
            ae.setHeaderText("Unexpected Error Occurred!");
            ae.setContentText(e.toString());
            ae.showAndWait();
        }

        return Optional.empty();
    }


    /**
     *
     */
    public void onLoadGameClicked() {
        // Get a player name
        final TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText("Please type a player name!");
        final Optional<String> name = dialog.showAndWait();

        // If one exists
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
                        final Alert ae = new Alert(Alert.AlertType.ERROR);
                        ae.setHeaderText("Unexpected Error Occurred!");
                        ae.setContentText(ex.toString());
                        ae.setResizable(true);
                        ae.showAndWait();
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
                final Alert ae = new Alert(Alert.AlertType.ERROR);
                ae.setHeaderText("Unexpected Error Occurred!");
                ae.setContentText(ex.toString());
                ae.showAndWait();
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
    public void onAboutClicked()throws IOException {
        final FXMLLoader loader = Main.loadAboutSectionStage();
        final Scene sc2 = new Scene(loader.load());
        Main.loadNewScene(sc2);
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
        // Load scene
        final FXMLLoader loader = Main.loadLeaderboardStage();
        final Scene sc = new Scene(loader.load());
        final LeaderboardController cont
                = loader.getController();

        // Register a new message of the day receiver
        final Consumer<String> motdPinger = cont::setMotdLabel;
        this.motdPingers.add(motdPinger);

        // Show the scene
        final Stage s = new Stage();
        s.initModality(Modality.APPLICATION_MODAL);
        s.setScene(sc);
        s.showAndWait();

        // When scene is closed removed the pinger for it
        this.motdPingers.remove(motdPinger);
    }
}
