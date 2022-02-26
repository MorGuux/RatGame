package gui.menu;

import game.event.impl.entity.specific.game.GameEndEvent;
import game.level.levels.RatGameLevel;
import game.level.levels.players.PlayerDataBase;
import game.level.levels.template.TemplateEditor;
import game.level.reader.RatGameFile;
import game.level.reader.RatGameSaveFile;
import game.level.reader.exception.InvalidArgsContent;
import game.level.reader.exception.RatGameFileException;
import game.motd.MOTDClient;
import game.player.Player;
import game.tile.exception.UnknownSpriteEnumeration;
import gui.about.AboutSectionController;
import gui.editor.LevelEditor;
import gui.editor.init.LevelEditorBuilder;
import gui.game.GameController;
import gui.leaderboard.LeaderboardController;
import gui.menu.dependant.level.LevelInputForm;
import gui.menu.dependant.level.type.LevelTypeForm;
import gui.menu.dependant.save.SaveSelectionController;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import launcher.Main;
import util.FileSystemUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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
 * Main menu scene controller this acts as an access point for which a user
 * can navigate the project accessing things like the editor or game.
 *
 * @author -Ry, Shashank, Ashraf
 * @version 0.9
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
     * Dropdown list of existing usernames.
     */
    @FXML
    private ComboBox<String> dropDownUsernames;

    /**
     * Toggle group for choice between new or existing user.
     */
    private final ToggleGroup userModeToggleGroup = new ToggleGroup();

    /**
     * Button for new user choice.
     */
    @FXML
    private ToggleButton newUserOption;

    /**
     * Button for existing user choice.
     */
    @FXML
    private ToggleButton existingUserOption;


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
                    Platform.runLater(() -> pinger.accept(actual));
                }

            }
        }, 0, UPDATE_RATE);


        // Shutdown the timer task when the scene is closed (has to be
        // initialised after load)
        Platform.runLater(() ->
                this.backgroundPane.getScene().getWindow().setOnCloseRequest(
                        (e) -> this.motdPinger.cancel()
                ));


        // Create the toggle group for the choice between existing or new user
        newUserOption.setToggleGroup(userModeToggleGroup);
        existingUserOption.setToggleGroup(userModeToggleGroup);

        // Select the new user option by default
        newUserOption.setSelected(true);
        dropDownUsernames.setDisable(true);

        // Add user toggle group selected listener
        userModeToggleGroup.selectedToggleProperty().addListener((obsValue,
                                                                  oldValue,
                                                                  newValue) -> {

            // One toggle must be selected at all times
            if (newValue == null) {
                oldValue.setSelected(true);

                // If a button different from the previous is selected
            } else {

                // If a new game is selected
                dropDownUsernames.setDisable(newValue.equals(newUserOption));
            }
        });


        // Try to get the player database in order to populate the dropdown
        try {
            this.dataBase = new PlayerDataBase();

            // Don't proceed if the player database could not be loaded.
        } catch (final IOException
                | URISyntaxException
                | InvalidArgsContent e) {

            final Alert ae = new Alert(Alert.AlertType.ERROR);
            ae.setHeaderText("Fatal Exception Occurred!");
            ae.setContentText(
                    "Program cannot continue as vital dependencies "
                            + "failed to load."
            );
            Stage stage = (Stage) ae.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream(
                    "rat1.png")));
            ae.showAndWait();
            System.exit(-1);
        }

        // Populate the dropdown of usernames
        final List<Player> players = dataBase.getPlayers();
        for (final Player p : players) {
            this.dropDownUsernames.getItems().add(p.getPlayerName());
        }

        dataBase.getPlayers().addListener((ListChangeListener<Player>) e -> {
            if (e.next()) {

                // Player added
                if (e.wasAdded()) {
                    e.getAddedSubList().forEach(this::addPlayer);

                    // Player removed
                } else if (e.wasRemoved()) {
                    e.getRemoved().forEach(this::removePlayer);
                }
            }
        });
    }

    /**
     * Adds a player to the drop-down usernames list.
     *
     * @param p The player to add.
     */
    private synchronized void addPlayer(final Player p) {
        this.dropDownUsernames.getItems().add(p.getPlayerName());
    }

    /**
     * Removes a player from the drop-down usernames list.
     *
     * @param p the player to remove.
     */
    private synchronized void removePlayer(final Player p) {
        this.dropDownUsernames.getItems().remove(p.getPlayerName());
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
    public void onStartGameClicked() {

        final Optional<String> username;
        final Stage stage = new Stage();
        final LevelTypeForm form;
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.getIcons().add(new Image(getClass().getResourceAsStream(
                "rat1.png")));

        // New user
        if (userModeToggleGroup.getSelectedToggle().equals(newUserOption)) {
            form = LevelTypeForm.initScene(stage);

            // Existing user
        } else {
            form = LevelTypeForm.init(stage, dropDownUsernames.getValue());
        }
        stage.showAndWait();
        username = form.getUsername();

        // Load scene if all data is present
        form.getIsCustomLevel().ifPresent((isCustomLevel) -> {
            if (username.isPresent()) {

                if (isCustomLevel) {
                    setupGameForCustomLevel(username.get());

                } else {
                    setupGameForBaseLevel(username.get());
                }
            }
        });
    }

    /**
     * Initialises the game for a custom level selection.
     *
     * @param username The players name.
     */
    private void setupGameForCustomLevel(final String username) {
        final File[] files = new File(
                TemplateEditor.CUSTOM_FILES_DIR
        ).listFiles();

        // We only evaluate the top level in the file structure hierarchy;
        // could make this better by using CommonsIO
        if (files != null) {
            // This is slow as hell since we're reading all the files; but I
            // don't want to modify our existing level selection form so uhm...
            // ¯\_(ツ)_/¯
            final List<RatGameFile> customLevels = new ArrayList<>();

            // Filter for Rat game default files
            Arrays.stream(files).filter(f -> {
                return f.getName().matches("(?i).*?\\.rgf");

                // And collect those that can compile into a rat game default
                // file object without causing issues.
            }).forEach(rgf -> {
                try {
                    customLevels.add(new RatGameFile(rgf));

                    // Always stack trace
                } catch (Exception e) {
                    e.printStackTrace();
                    alertBrokenLevel(rgf);
                }
            });

            // Ensure that there is at least one level to choose from
            if (customLevels.size() == 0) {
                final Alert ae = new Alert(Alert.AlertType.INFORMATION);
                ae.setHeaderText("No custom levels found!");
                ae.setContentText("You don't have any custom levels to play.");
                Stage stage = (Stage) ae.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(getClass().getResourceAsStream(
                        "rat1.png")));
                ae.showAndWait();


                // Load custom level selection
            } else {
                final LevelInputForm form = LevelInputForm.loadAndWait(
                        new Stage(),
                        customLevels.toArray(new RatGameFile[0])
                );

                // Start game for custom level
                if (form.getLevelSelection().isPresent()) {
                    initGameFor(
                            new Player(username),
                            form.getLevelSelection().get(),
                            dataBase
                    );
                }
            }
        }
    }

    /**
     * Alerts that some file is broken and cannot be parsed as a RatGameFile.
     *
     * @param f The file that is broken.
     */
    private void alertBrokenLevel(final File f) {
        final Alert ae = new Alert(Alert.AlertType.WARNING);
        ae.setHeaderText("Broken Level File Detected!");
        ae.setContentText(String.format(
                "Evaluation of the level named: [%s] concluded that it was "
                        + "broken.",
                f.getName()
        ));
        ae.showAndWait();
    }

    /**
     * Creates and initialises the game for an existing or non-existing
     * player. Using the default game level selection as the basis.
     *
     * @param name The players name that was typed.
     */
    private void setupGameForBaseLevel(final String name) {
        // Show levels they've unlocked
        if (name != null && isSafeName(name)) {

            if (dataBase.isPlayerPresent(name)) {
                final Player p = dataBase.getPlayer(name);
                p.setCurrentScore(0);
                p.setPlayTime(0);

                // Get level selection and start game if present
                final Optional<RatGameFile> level =
                        getLevelSelectionForPlayer(p);
                level.ifPresent((lvl) -> initGameFor(p, lvl, dataBase));

                // Player should be created
            } else {
                final Player p = new Player(name);

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
     * @param p        The player who is playing.
     * @param lvl      The level they are playing.
     * @param dataBase The File used to store existing player profiles.
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
            s.getIcons().add(new Image(getClass().getResourceAsStream(
                    "rat1.png")));
            game.startGame(s);

            final Optional<GameEndEvent> gameState = game.getGameResult();

            // If player has won mark this level as completed
            if (gameState.isPresent()
                    && gameState.get().isGameWon()
                    && !game.getLevel().isCustomLevel()) {
                final String idName
                        = lvl.getDefaultProperties().getIdentifierName();
                p.setLevelCompleted(RatGameLevel.getLevelFromName(idName));
            }

            // Add the player (reselect as update does index -1 which is the
            // wrong index for the current player)
            dataBase.commitPlayer(p);
            this.dropDownUsernames
                    .getSelectionModel()
                    .select(p.getPlayerName());

            // No longer needed in the game scene
            this.motdPingers.remove(motdPinger);

        } catch (final IOException e) {
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
            Stage stage = (Stage) ae.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream(
                    "rat1.png")));
            ae.showAndWait();
        }

        return Optional.empty();
    }

    /**
     * This method is called when the user clicks the "Load Game" button.
     * Asks the user for their name, and then offers any save games they have
     * (if any).
     */
    public void onLoadGameClicked() {
        // Get a player name
        final Optional<String> name;
        if (dropDownUsernames.getValue() == null) {
            final TextInputDialog dialog = new TextInputDialog();
            dialog.setHeaderText("Please type a player name!");
            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream(
                    "rat1.png")));
            name = dialog.showAndWait();
        } else {
            name = Optional.of(dropDownUsernames.getValue());
        }

        // If one exists
        if (name.isPresent()) {
            final String username = name.get();


            // Get all save files
            final Path[] saveFiles = FileSystemUtil.getAllSaveFiles();

            // Parse all valid save files
            final List<RatGameSaveFile> saves = new ArrayList<>();
            Arrays.stream(saveFiles).forEach(i -> {
                try {
                    saves.add(new RatGameSaveFile(i.toFile()));
                } catch (IOException
                        | RatGameFileException
                        | UnknownSpriteEnumeration ex) {
                    ex.printStackTrace();
                    final Alert ae = new Alert(Alert.AlertType.ERROR);
                    ae.setHeaderText("Unexpected Error Occurred!");
                    ae.setContentText(ex.toString());
                    ae.setResizable(true);
                    Stage stage = (Stage) ae.getDialogPane().getScene()
                            .getWindow();
                    stage.getIcons().add(new Image(getClass()
                            .getResourceAsStream("rat1.png")));
                    ae.showAndWait();
                }
            });

            // Only those saves with the correct username need to be shown
            saves.removeIf(i ->
                    !i.getPlayer().getPlayerName().equals(username)
            );

            // If there are no saves, show an error
            if (saves.isEmpty()) {
                final Alert ae = new Alert(Alert.AlertType.ERROR);
                ae.setHeaderText("No Save Files Found!");
                ae.setContentText("No save files were found for the "
                        + "specified player.");
                Stage stage = (Stage) ae.getDialogPane().getScene().getWindow();
                stage.getIcons().add(new Image(getClass().getResourceAsStream(
                        "rat1.png")));
                ae.showAndWait();
            } else {
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
            Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream(
                    "rat1.png")));
            alert.showAndWait();
        }
    }

    /**
     * Opens a new window containing the about data for whom the project was
     * developed by and some nice little information outside it.
     */
    public void onAboutClicked() {
        final FXMLLoader loader
                = new FXMLLoader(AboutSectionController.SCENE_FXML);

        try {
            final Scene scene = new Scene(loader.load());
            final AboutSectionController cont = loader.getController();

            final Consumer<String> pinger = cont::setMotdLabel;
            this.motdPingers.add(pinger);

            final Stage s = new Stage();
            s.setScene(scene);
            s.getIcons().add(new Image(getClass().getResourceAsStream(
                    "rat1.png")));

            s.showAndWait();
            motdPingers.remove(pinger);

        } catch (IOException e) {

            final Alert ae = new Alert(Alert.AlertType.ERROR);
            ae.setHeaderText("Failed to load the About Section!");
            ae.setContentText("Some issue stopped the about section from "
                    + "loading see: " + e.getMessage());
            Stage stage = (Stage) ae.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream(
                    "rat1.png")));
            ae.showAndWait();
        }

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
     * Displays the leaderboard.
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
        s.getIcons().add(new Image(getClass().getResourceAsStream(
                "rat1.png")));
        s.showAndWait();

        // When scene is closed removed the pinger for it
        this.motdPingers.remove(motdPinger);
    }

    /**
     * Loads the level editor scene after first prompting for which level to
     * edit.
     */
    public void onOpenEditorClicked() {
        this.backgroundPane.getScene().getWindow().hide();

        final Stage s = new Stage();
        s.initModality(Modality.APPLICATION_MODAL);
        s.getIcons().add(new Image(getClass()
                .getResourceAsStream("rat1.png")));

        try {
            final LevelEditorBuilder builder = new LevelEditorBuilder(s);

            // Builder.build injects the level editor scene into the stage s
            // the try is known as: try-with-resources.
            // It calls the LevelEditor#close method the moment we're about
            // to leave the try block.
            try (LevelEditor editor = builder.build()) {
                s.showAndWait();
            }

            // Stack trace isn't needed here.
        } catch (final Exception e) {
            final Alert ae = new Alert(Alert.AlertType.ERROR);
            ae.setHeaderText("Error!!!");
            ae.setContentText("The level editor could not load due to the "
                    + "following reason: " + e.getMessage()
            );
            Stage stage = (Stage) ae.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image(getClass().getResourceAsStream(
                    "rat1.png")));
            ae.showAndWait();
        }

        // Reshow main stage
        final Stage stage
                = (Stage) this.backgroundPane.getScene().getWindow();
        stage.show();
    }
}
