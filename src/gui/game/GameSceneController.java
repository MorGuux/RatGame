package gui.game;

import game.RatGame;
import game.entity.subclass.deathRat.DeathRat;
import game.level.Level;
import game.level.reader.RatGameFile;
import game.level.reader.module.GameProperties;
import game.player.Player;
import game.tile.Tile;
import gui.game.dependant.itemview.ItemViewController;
import gui.game.dependant.tilemap.GameMap;
import gui.game.dependant.tilemap.GridPaneFactory;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import launcher.Main;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Main Game Window Controller; This would implement the 'RatGameActionListener'
 * which would be the bridge required to get events from the game to the GUI.
 *
 * @author -Ry
 * Copyright: N/A
 * @version 0.4
 */
public class GameSceneController {

    /**
     * Hardcode the Scene Object Hierarchy Resource to the Controller so that
     * it can be accessed.
     */
    private static final URL SCENE_FXML =
            GameSceneController.class.getResource("GameScene.fxml");

    /**
     * Game stack pane that gives depth to the nodes on the game.
     */
    @FXML
    private StackPane gameStackPane;

    /**
     * Scroll pane that the game is played inside of.
     */
    @FXML
    private ScrollPane gameScrollPane;

    /**
     * This pane contains all the nodes for the scene it is the 'root' so
     * to call it.
     */
    @FXML
    private BorderPane mainPane;

    /**
     * Name of the player.
     */
    @FXML
    private Label playerNameLabel;

    /**
     * Message of the day.
     */
    @FXML
    private Label messageOfTheDayLabel;

    /**
     * Game background pane, this is where we would display the tile map.
     */
    @FXML
    private Pane gameBackground;

    /**
     * Game foreground, this is where we would display the entities in the
     * game.
     */
    @FXML
    private Pane gameForeground;

    /**
     * Pane that stands in front of the game foreground. Unused but the idea
     * is that we can have the leaderboard be shown in game on a button press.
     */
    @FXML
    private Pane frontOfAllPane;

    /**
     * Label representing how much time is remaining.
     */
    @FXML
    private Label timeRemainingLabel;

    /**
     * Number of rats in the game.
     */
    @FXML
    private Label numberOfRatsLabel;

    /**
     * The total score the player has amassed
     */
    @FXML
    private Label scoreLabel;

    /**
     * All items available to the player to use.
     */
    @FXML
    private VBox itemVbox;

    /**
     * The player who is playing the rat game.
     */
    private Player player;

    /**
     * The level the player is playing.
     */
    private RatGameFile level;

    /**
     * The underlying rat game.
     */
    private RatGame game;

    /**
     * Underlying game tile map.
     */
    private GameMap tileMap;

    /**
     * Method used to initiate the game with the target player. Loads the
     * game and initiates all essential data and then waits. To finally
     * initiate the game call {@link GameSceneController#startGame}.
     *
     * @param player The player whose playing the RatGame.
     * @param level  The level that the player is playing.
     * @return Newly constructed and initiated Rat game scene controller.
     * @throws NullPointerException If any parameter is null.
     * @throws IOException          If one occurs whilst setting up the scene.
     */
    public static GameSceneController loadAndGet(final Player player,
                                                 final RatGameFile level)
            throws IOException, NullPointerException {

        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);
        loader.load();

        Objects.requireNonNull(player);
        Objects.requireNonNull(level);

        final GameSceneController c = loader.getController();
        c.setGameData(player, level);
        c.loadData();

        return c;
    }

    /**
     * Set the target player and the target level.
     *
     * @param player The player who is playing the game.
     * @param level  The level that the player is playing.
     */
    private void setGameData(final Player player,
                             final RatGameFile level) {
        this.player = player;
        this.level = level;

        // Bind game scene sizes
        this.gameForeground.heightProperty().addListener((val, old, cur) -> {
            this.gameBackground.setMinHeight(cur.doubleValue());
            this.gameBackground.setMaxHeight(cur.doubleValue());
            this.gameBackground.setPrefHeight(cur.doubleValue());

            this.gameScrollPane.setMaxHeight(cur.doubleValue());
        });
        this.gameForeground.widthProperty().addListener((val, old, cur) -> {
            this.gameBackground.setMinWidth(cur.doubleValue());
            this.gameBackground.setMaxWidth(cur.doubleValue());
            this.gameBackground.setPrefHeight(cur.doubleValue());

            this.gameScrollPane.setMaxWidth(cur.doubleValue());
        });
    }

    /**
     * Loads all the game data into the scene displaying it visually.
     */
    private void loadData() {
        this.playerNameLabel.setText(player.getPlayerName());
        final GameProperties prop = level.getDefaultProperties();
        final int timeScaleFactor = 1000;
        this.timeRemainingLabel.setText(
                "Time Remaining: "
                        + prop.getTimeLimit() / timeScaleFactor
        );

        this.numberOfRatsLabel.setText(
                "Max Rats: "
                        + prop.getMaxRats()
        );

        this.scoreLabel.setText(
                "Total Score: "
                        + player.getCurrentScore()
        );

        DeathRat rat = new DeathRat(0, 0);
        for (int i = 0; i < 10; ++i) {
            ItemViewController c = ItemViewController.loadView();
            c.setItemImage(new Image(rat.getDisplaySprite().toExternalForm()));
            c.setItemName(rat.getClass().getSimpleName());
            c.setCurrentUsages(i);
            c.setMaxUsages(10);
            this.itemVbox.getChildren().add(c.getRoot());
        }

        loadMap();
    }

    /**
     * Loads into the background pane the game map.
     */
    private void loadMap() {
        final Level p = level.getLevel();
        this.tileMap = new GameMap(
                p.getRows(),
                p.getColumns(),
                new GridPaneFactory.CenteredGridPane()
        );

        for (Tile[] tiles : p.getTiles()) {
            for (Tile t : tiles) {
                this.tileMap.setNodeAt(
                        t.getRow(),
                        t.getCol(),
                        t.getFXSpriteView()
                );
            }
        }

        tileMap.displayIn(gameBackground);
    }

    /**
     * Starts the Rat game and displays it into the provided stage then waits
     * until the user closes the game.
     *
     * @param s The stage to display the game on.
     */
    public void startGame(final Stage s) {
        final Scene scene = new Scene(mainPane);
        s.setScene(scene);
        s.showAndWait();
    }

    /**
     * Sets the style for this scene to the application default style.
     */
    private void setStyleSheet() {
        mainPane.getScene().getRoot().getStylesheets().clear();
        mainPane.getScene().getRoot().getStylesheets().add(
                Main.getCurrentStyle()
        );
    }

    /**
     * Pauses the game.
     */
    @FXML
    private void onPauseClicked() {
    }

    /**
     * Saves the game.
     */
    @FXML
    private void onSaveClicked() {
    }

    /**
     * Hides the scroll bars.
     */
    @FXML
    private void onHideScrollBarsClicked() {
        ScrollPane.ScrollBarPolicy policy
                = ScrollPane.ScrollBarPolicy.AS_NEEDED;

        if (this.gameScrollPane.getVbarPolicy() == policy) {
            policy = ScrollPane.ScrollBarPolicy.NEVER;
        }

        this.gameScrollPane.setVbarPolicy(policy);
        this.gameScrollPane.setHbarPolicy(policy);
    }

    /**
     * Zooms in on the game.
     */
    @FXML
    private void onZoomIn() {
        // Not sure if I want to zoom into the game, or have the scroll pane
        // zoom; the latter is nicer since it makes the game take up more
        // space on the screen

        final double x = this.gameBackground.getScaleX();
        final double y = this.gameBackground.getScaleY();

        this.gameBackground.setScaleX(x + 0.1);
        this.gameBackground.setScaleY(y + 0.1);

        this.gameForeground.setScaleX(x + 0.1);
        this.gameForeground.setScaleY(y + 0.1);
    }

    /**
     * Zooms out in the game.
     */
    @FXML
    private void onZoomOut() {
        final double x = this.gameBackground.getScaleX();
        final double y = this.gameBackground.getScaleY();

        this.gameBackground.setScaleX(x - 0.1);
        this.gameBackground.setScaleY(y - 0.1);

        this.gameForeground.setScaleX(x - 0.1);
        this.gameForeground.setScaleY(y - 0.1);
    }

    /**
     * Resets the game zoom state.
     */
    @FXML
    private void onResetZoom() {
        this.gameBackground.setScaleX(1);
        this.gameBackground.setScaleY(1);

        this.gameForeground.setScaleX(1);
        this.gameForeground.setScaleY(1);
    }
}
