package gui.game;

import game.RatGame;
import game.RatGameBuilder;
import game.entity.subclass.deathRat.DeathRat;
import game.contextmap.CardinalDirection;
import game.contextmap.ContextualMap;
import game.entity.Entity;
import game.entity.Item;
import game.entity.subclass.noentry.NoEntry;
import game.entity.subclass.rat.Rat;
import game.event.GameEvent;
import game.event.adapter.AbstractGameAdapter;
import game.event.impl.entity.specific.game.GameEndEvent;
import game.event.impl.entity.specific.game.GamePausedEvent;
import game.event.impl.entity.specific.game.GameStateUpdateEvent;
import game.event.impl.entity.specific.general.EntityDeathEvent;
import game.event.impl.entity.specific.general.EntityMovedEvent;
import game.event.impl.entity.specific.general.EntityOccupyTileEvent;
import game.event.impl.entity.specific.general.SpriteChangeEvent;
import game.event.impl.entity.specific.item.GeneratorUpdateEvent;
import game.event.impl.entity.specific.load.EntityLoadEvent;
import game.event.impl.entity.specific.load.GameLoadEvent;
import game.event.impl.entity.specific.load.GeneratorLoadEvent;
import game.event.impl.entity.specific.player.ScoreUpdateEvent;
import game.level.reader.RatGameFile;
import game.player.Player;
import game.tile.Tile;
import game.tile.base.path.Path;
import gui.game.dependant.entitymap.redone.EntityMap;
import gui.game.dependant.itemview.ItemViewController;
import gui.game.dependant.tilemap.GameMap;
import gui.game.dependant.tilemap.GridPaneFactory;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.util.Duration;
import launcher.Main;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
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
     * Scroll pane that the game is played inside.
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
     * All the game generators and their current usage states.
     */
    private HashMap<Class<?>, ItemViewController> generatorMap;
    
    /**
     * Contextual map of TileDataNodes.
     */
    private ContextualMap contextMap;

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
        this.generatorMap = new HashMap<>();

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

        final RatGameBuilder b = new RatGameBuilder(
                this,
                this.level,
                this.player
        );
        
        this.game = b.buildGame();

        setOnDragDroppedEventListener();
        setOnDragOverEventListener();
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
     * Set the message of the day text label to the provided text.
     *
     * @param s The message of the day to display.
     */
    public void setMotdText(final String s) {
        this.messageOfTheDayLabel.setText(s);
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
     * Makes the Game scroll pane view wider.
     *
     * @param event The mouse event to handle.
     */
    @FXML
    private void onZoomWidth(final MouseEvent event) {
        final MouseButton button = event.getButton();

        if (button.equals(MouseButton.PRIMARY)) {
            this.gameScrollPane.setScaleX(
                    this.gameScrollPane.getScaleX() + 0.05
            );

        } else {
            this.gameScrollPane.setScaleX(
                    this.gameScrollPane.getScaleX() - 0.05
            );
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
     * Displays the leaderboard for this level.
     *
     * @param actionEvent
     */
    public void onShowScoreboardClicked(final ActionEvent actionEvent) {

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
        System.out.println("GAME PAUSED!");
        e.getEventAuthor().spawnEntity(new DeathRat(1,1));
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
        this.entityMap = new EntityMap(
                e.getMapRows(),
                e.getMapColumns()
        );

        final GridPane pane = this.entityMap.getRoot();
        pane.getRowConstraints()
                .forEach(i -> i.setMinHeight(Tile.DEFAULT_SIZE));
        pane.getColumnConstraints()
                .forEach(i -> i.setMinWidth(Tile.DEFAULT_SIZE));

        this.gameForeground.getChildren().add(this.entityMap.getRoot());

        // Load game map
        this.tileMap = new GameMap(
                e.getMapRows(),
                e.getMapColumns(),
                new GridPaneFactory.CenteredGridPane()
        );
        for (Tile[] tiles : e.getTileMap()) {
            for (Tile tile : tiles) {
                tileMap.setNodeAt(
                        tile.getRow(),
                        tile.getCol(),
                        tile.getFXSpriteView()
                );
            }
        }
        this.tileMap.displayIn(gameBackground);

        this.playerNameLabel.setText(e.getPlayerName());
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
        final Tooltip tip = new Tooltip(e.toString());
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
            final ItemViewController c = ItemViewController.loadView(
                    e.getTargetClass());
            c.setMaxUsages(e.getMaxUsages());
            c.setItemImage(new Image(e.getDisplaySprite().toExternalForm()));
            c.setCurrentUsages(e.getCurUsages());
            c.setItemName(e.getTargetClass().getSimpleName());

            c.setOnDragDetectedEventListener();

            c.setStylesheet(Main.getCurrentStyle());

            itemVbox.getChildren().add(c.getRoot());
            this.generatorMap.put(e.getTargetClass(), c);

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

        final ImageView view = entityMap.getOriginView(e.getEntityID());
        final Tooltip t = new Tooltip(e.toString());
        t.setShowDelay(Duration.ONE);
        Tooltip.install(view, t);

        final AudioClip c = new AudioClip(
                getClass().getResource("EventAudio/Gas.wav").toExternalForm()
        );
        c.setVolume(0.05);
        c.setCycleCount(0);
        c.play();
    }

    /**
     * @param e
     */
    @Override
    public void onEntityOccupyTileEvent(EntityOccupyTileEvent e) {
        final ImageView view = new ImageView();

        // Tooltip which is immediately shown
        final Tooltip tip = new Tooltip(e.toString());
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
        entityMap.setImage(e.getEntityID(), null);

    }

    /**
     * @param e
     */
    @Override
    public void onSpriteChangeEvent(SpriteChangeEvent e) {
        if (e.getImageResource() != null) {
            this.entityMap.setImage(
                    e.getEntityID(),
                    new Image(e.getImageResource().toExternalForm())
            );
        } else {
            this.entityMap.setImage(
                    e.getEntityID(),
                    null);
        }

    }

    /**
     * @param e
     */
    @Override
    public void onGeneratorUpdate(GeneratorUpdateEvent e) {
        final ItemViewController cont
                = generatorMap.get(e.getTargetClass());

        final int maxTime = e.getRefreshTime();
        final int curTime = e.getCurRefreshTime();

        if (e.getCurUsages() != e.getMaxUsages()) {
            cont.setCurrentProgress((double) curTime / maxTime);

        } else {
            cont.setCurrentProgress(1.0);
        }

        cont.setCurrentUsages(e.getCurUsages());
    }

    /**
     * @param e
     */
    @Override
    public void onGameStateUpdate(GameStateUpdateEvent e) {
        // Matches everything but those specified
        final String baseRegex = "[^a-zA-Z\\s:]+";

        // Replace only the numerical part of the labels
        String labelText = timeRemainingLabel.getText();
        this.timeRemainingLabel.setText(labelText.replaceAll(
                baseRegex, String.valueOf((int) e.getClearTimeSeconds())
        ));

        labelText = numberOfRatsLabel.getText();
        this.numberOfRatsLabel.setText(labelText.replaceAll(
                baseRegex, String.valueOf(e.getNumHostileEntities())
        ));

        labelText = scoreLabel.getText();
        this.scoreLabel.setText(labelText.replaceAll(
                baseRegex, String.valueOf(player.getCurrentScore())
        ));

        System.out.printf("Male: %s, Female: %s\n",
                e.getNumMaleHostileEntities(),
                e.getNumFemaleHostileEntities());
    }

    /**
     * Set onDragOver EventListener for gameStackPane, so it checks if the
     * destination is one of the ItemViews.
     */
    public void setOnDragOverEventListener() {
        gameStackPane.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                // Mark the drag event as acceptable by the gameStackPane.
                dragEvent.acceptTransferModes(TransferMode.ANY);
                // Mark the event as dealt.
                dragEvent.consume();
            }
        });
    }

    /**
     * Set onDragDropped EventListener for gameStackPane, so it fires item drop.
     */
    public void setOnDragDroppedEventListener() {
        gameStackPane.setOnDragDropped(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent dragEvent) {
                itemDropped(dragEvent);
                // Mark the event as dealt.
                dragEvent.consume();
            }
        });
    }

    private void itemDropped(DragEvent event) {
        double x = event.getX();
        double y = event.getY();

        Class<? extends Item> item = (Class<? extends Item>)
                event.getDragboard().getContent(ItemViewController.DATA_FORMAT);

        //48 x 48 pixels
        int row = (int) Math.floor(y / Tile.DEFAULT_SIZE);
        int col = (int) Math.floor(x / Tile.DEFAULT_SIZE);
        System.out.printf("%s should be placed at (%d, %d).%n", item.toString(),
                row, col);



        game.useItem((Class<Item>)item, row, col);

    }
}
