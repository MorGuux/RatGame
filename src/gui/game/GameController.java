package gui.game;

import game.RatGame;
import game.RatGameBuilder;
import game.event.GameEvent;
import game.event.adapter.AbstractGameAdapter;
import game.event.impl.entity.specific.game.GameEndEvent;
import game.event.impl.entity.specific.game.GamePausedEvent;
import game.event.impl.entity.specific.general.EntityDeathEvent;
import game.event.impl.entity.specific.general.EntityMovedEvent;
import game.event.impl.entity.specific.general.EntityOccupyTileEvent;
import game.event.impl.entity.specific.general.SpriteChangeEvent;
import game.event.impl.entity.specific.item.GeneratorUpdateEvent;
import game.event.impl.entity.specific.load.EntityLoadEvent;
import game.event.impl.entity.specific.load.GameLoadEvent;
import game.event.impl.entity.specific.load.GeneratorLoadEvent;
import game.event.impl.entity.specific.player.ScoreUpdateEvent;
import game.generator.ItemGenerator;
import game.generator.RatItemInventory;
import game.level.Level;
import game.level.reader.RatGameFile;
import game.level.reader.module.GameProperties;
import game.player.Player;
import game.tile.Tile;
import gui.game.dependant.entitymap.redone.EntityMap;
import gui.game.dependant.itemview.ItemViewController;
import gui.game.dependant.tilemap.GameMap;
import gui.game.dependant.tilemap.GridPaneFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
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
public class GameController extends AbstractGameAdapter {

    /**
     * Hardcode the Scene Object Hierarchy Resource to the Controller so that
     * it can be accessed.
     */
    private static final URL SCENE_FXML =
            GameController.class.getResource("GameScene.fxml");


    /**
     * The pause button for the scene.
     */
    @FXML
    private Button pauseButton;

    /**
     * The save button for the scene.
     */
    @FXML
    private Button saveButton;

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
     * Map of entities and their javafx node representations.
     */
    private EntityMap entityMap;

    /**
     * Method used to initiate the game with the target player. Loads the
     * game and initiates all essential data and then waits. To finally
     * initiate the game call {@link GameController#startGame}.
     *
     * @param player The player whose playing the RatGame.
     * @param level  The level that the player is playing.
     * @return Newly constructed and initiated Rat game scene controller.
     * @throws NullPointerException If any parameter is null.
     * @throws IOException          If one occurs whilst setting up the scene.
     */
    public static GameController loadAndGet(final Player player,
                                            final RatGameFile level)
            throws IOException, NullPointerException {

        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);
        loader.load();

        Objects.requireNonNull(player);
        Objects.requireNonNull(level);

        final GameController c = loader.getController();
        c.setGameData(player, level);
        c.loadData();
        Platform.runLater(c::setStyleSheet);

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

        // Disable save button (only allowed to save whilst paused)
        saveButton.setDisable(true);
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

        loadMap();
        loadEntityMap();


        RatItemInventory generators = level.getDefaultGenerator();

        for (ItemGenerator<?> generator : generators.getGenerators()) {
            Platform.runLater(() -> {
                GeneratorLoadEvent event = new GeneratorLoadEvent(generator);
                this.onAction(event);
            });
        }

    }

    /**
     *
     */
    private void loadEntityMap() {
        final GameProperties prop = level.getDefaultProperties();
        this.entityMap = new EntityMap(
                prop.getRows(),
                prop.getColumns()
        );

        this.entityMap.getRoot().getColumnConstraints()
                .forEach(i -> i.setMinWidth(Tile.DEFAULT_SIZE));

        this.entityMap.getRoot().getRowConstraints()
                .forEach(i -> i.setMinHeight(Tile.DEFAULT_SIZE));

        this.gameForeground.getChildren().add(this.entityMap.getRoot());

        // Build the game ready to play
        final RatGameBuilder b = new RatGameBuilder(
                this,
                this.level,
                this.player
        );
        this.game = b.buildGame();
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
        this.game.startGame();
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
        this.saveButton.setDisable(!this.saveButton.isDisabled());

        // Start game if paused.
        if (this.game.isGamePaused()) {
            this.game.startGame();
            this.pauseButton.setText("Pause");

            // Pause game if not over
        } else if (!this.game.isGameOver()) {
            this.game.pauseGame();
            this.pauseButton.setText("Start");
        }
    }

    /**
     * Saves the current game.
     */
    @FXML
    private void onSaveClicked() {
        this.saveButton.setDisable(true);
        this.pauseButton.setDisable(true);

        new Thread(() -> {
            try {
                Thread.sleep(3000);

                this.saveButton.setDisable(false);
                this.pauseButton.setDisable(false);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
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
    private void onZoomIn(final MouseEvent e) {

        // Zoom in on game scene
        if (e.getButton().equals(MouseButton.PRIMARY)) {
            final double x = this.gameBackground.getScaleX();
            final double y = this.gameBackground.getScaleY();

            this.gameBackground.setScaleX(x + 0.1);
            this.gameBackground.setScaleY(y + 0.1);

            this.gameForeground.setScaleX(x + 0.1);
            this.gameForeground.setScaleY(y + 0.1);

            // Zoom in on scroll pane
        } else {
            final double x = this.gameScrollPane.getScaleX();
            final double y = this.gameScrollPane.getScaleY();

            this.gameScrollPane.setScaleX(x + 0.1);
            this.gameScrollPane.setScaleY(y + 0.1);
        }
    }

    /**
     * Zooms out in the game.
     */
    @FXML
    private void onZoomOut(final MouseEvent e) {
        if (e.getButton().equals(MouseButton.PRIMARY)) {
            final double x = this.gameBackground.getScaleX();
            final double y = this.gameBackground.getScaleY();

            this.gameBackground.setScaleX(x - 0.1);
            this.gameBackground.setScaleY(y - 0.1);

            this.gameForeground.setScaleX(x - 0.1);
            this.gameForeground.setScaleY(y - 0.1);

            // Zoom out on scroll pane
        } else {
            final double x = this.gameScrollPane.getScaleX();
            final double y = this.gameScrollPane.getScaleY();

            this.gameScrollPane.setScaleX(x - 0.1);
            this.gameScrollPane.setScaleY(y - 0.1);
        }
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

        this.gameScrollPane.setScaleX(1);
        this.gameScrollPane.setScaleY(1);
    }


    /**
     * Delegates an event to its handler.
     *
     * @param event The event to delegate.
     */
    @Override
    public void onAction(GameEvent<?> event) {
        Platform.runLater(() -> super.onAction(event));
    }

    /**
     * Game paused event.
     *
     * @param e Pause event.
     */
    @Override
    public void onGamePaused(GamePausedEvent e) {

    }

    /**
     * @param e
     */
    @Override
    public void onGameEndEvent(GameEndEvent e) {

    }

    /**
     * @param e
     */
    @Override
    public void onGameLoadEvent(GameLoadEvent e) {

    }

    /**
     * @param e
     */
    @Override
    public void onEntityLoadEvent(EntityLoadEvent e) {
        final ImageView view = new ImageView();
        view.setImage(new Image(e.getImageResource().toExternalForm()));
        view.setSmooth(false);
        view.setFitWidth(Tile.DEFAULT_SIZE);
        view.setFitHeight(Tile.DEFAULT_SIZE);

        // Tooltip which is immediately shown
        final Tooltip tip = new Tooltip("Entity: " + e.getEntityID());
        Tooltip.install(view, tip);
        tip.setShowDuration(Duration.INDEFINITE);
        tip.setShowDelay(Duration.ZERO);


        this.entityMap.addView(
                e.getEntityID(),
                view,
                e.getRow(),
                e.getCol()
        );
    }

    /**
     * @param e
     */
    @Override
    public void onGeneratorLoadEvent(GeneratorLoadEvent e) {
        try {
            final ItemViewController c = ItemViewController.loadView();
            c.setMaxUsages(e.getMaxUsages());
            c.setItemImage(new Image(e.getDisplaySprite().toExternalForm()));
            c.setCurrentUsages(e.getCurUsages());
            c.setItemName(e.getTargetClass().getSimpleName());

            c.setStylesheet(Main.getCurrentStyle());
            itemVbox.getChildren().add(c.getRoot());
        } catch (Exception ex) {
            System.out.println("Error loading inventory item: " +
                    e.getTargetClass().getSimpleName());
        }
    }

    /**
     * @param e
     */
    @Override
    public void onScoreUpdate(ScoreUpdateEvent e) {
        this.scoreLabel.setText("Score: " + e.getPlayer().getCurrentScore());
    }

    /**
     * @param e
     */
    @Override
    public void onEntityMovedEvent(EntityMovedEvent e) {
        entityMap.setPosition(
                e.getEntityID(),
                e.getRow(),
                e.getCol(),
                e.getDirection()
        );
    }

    /**
     * @param e
     */
    @Override
    public void onEntityOccupyTileEvent(EntityOccupyTileEvent e) {
        final ImageView view = new ImageView();

        // Tooltip which is immediately shown
        final Tooltip tip = new Tooltip("Entity: " + e.getEntityID());
        Tooltip.install(view, tip);
        tip.setShowDuration(Duration.INDEFINITE);
        tip.setShowDelay(Duration.ZERO);

        view.setImage(new Image(e.getImageResource().toExternalForm()));
        view.setSmooth(false);
        view.setFitWidth(Tile.DEFAULT_SIZE);
        view.setFitHeight(Tile.DEFAULT_SIZE);

        this.entityMap.occupyPosition(
                e.getEntityID(),
                view,
                e.getOccupiedRow(),
                e.getOccupiedCol()
        );
    }

    /**
     * @param e
     */
    @Override
    public void onEntityDeathEvent(EntityDeathEvent e) {

    }

    /**
     * @param e
     */
    @Override
    public void onSpriteChangeEvent(SpriteChangeEvent e) {

    }

    /**
     * @param e
     */
    @Override
    public void onGeneratorUpdate(GeneratorUpdateEvent e) {

    }
}
