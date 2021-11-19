package gui.game;

import game.tile.grass.Grass;
import game.tile.grass.GrassSprite;
import gui.game.dependant.itemview.ItemViewController;
import gui.game.dependant.tilemap.GameMap;
import gui.game.dependant.tilemap.GridPaneFactory;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import launcher.Main;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * Main Game Window Controller; This would implement the 'RatGameActionListener'
 * which would be the bridge required to get events from the game to the GUI.
 *
 * @author -Ry
 * Copyright: N/A
 * @version 0.2
 */
public class GameSceneController implements Initializable {

    /**
     * Hardcode the Scene Object Hierarchy Resource to the Controller so that
     * it can be accessed.
     */
    public static final URL SCENE_FXML =
            GameSceneController.class.getResource("GameScene.fxml");

    /**
     * Main node which is the Root of this Node hierarchy.
     */
    @FXML
    private BorderPane mainPane;

    /**
     * Game Background Pane this contains things such as the Game Map.
     */
    @FXML
    private Pane gameBackground;

    /**
     * Game Foreground Pane this contains things such as the game entities.
     */
    @FXML
    private Pane gameForeground;

    /**
     * Contains all the Nodes representing Items of the Game.
     */
    @FXML
    private VBox itemVbox;

    /**
     * Delete once testing isn't needed anymore.
     */
    private List<ItemViewController> temporaryList;

    private GameMap map;

    /**
     * Initialises the main scene.
     *
     * @param url            FXML File used to load this controller.
     * @param resourceBundle Not sure, but should be null in our case.
     */
    @Override
    public void initialize(final URL url,
                           final ResourceBundle resourceBundle) {
        Platform.runLater(this::setStyleSheet);

        // Test code
        temporaryList = new ArrayList<>();
        createItems();
        final Timer t = new Timer();
        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> updateRandomItemData());
            }
        }, 0, 30);

        Platform.runLater(this::createTileMap);

        Grass e = Grass.build("[GRASS, (TURN_F_LEFT, 0, 0)]");
        System.out.printf("[%s, %s, %s]%n", e.getRow(), e.getCol(),
                e.isCanInteract());

        // This throws an exception
        try {
            Grass.build("[GRASS, (EnumClass, 0, 0)]");
        } catch (Exception ex) {
            System.out.println("Threw exception as expected.");
        }
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
     * Temporary method that allows us to get back to the main menu.
     */
    @FXML
    protected void loadPreviousScene() {
        Main.reloadMainMenu();
    }

    /**
     * Temporary method.
     */
    private void createItems() {
        // Test code
        System.out.println(getClass().getSimpleName() + "::initialize");
        final Random r = new Random();
        final int bound = 100;
        for (int i = 0; i < 4; i++) {
            final FXMLLoader loader =
                    new FXMLLoader(ItemViewController.SCENE_FXML);
            try {
                final Parent p = loader.load();
                final ItemViewController c = loader.getController();
                final int max = r.nextInt(bound) + 1;

                c.setMaxUsages(max);
                c.setCurrentUsages(r.nextInt(max));
                c.setStylesheet(Main.getCurrentStyle());

                temporaryList.add(c);
                this.itemVbox.getChildren().add(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateRandomItemData() {
        final Random r = new Random();
        final int bound = temporaryList.size();

        final ItemViewController c = temporaryList.get(r.nextInt(bound));
        c.setCurrentUsages(r.nextInt(c.getMaxUsages()));
        c.setItemName("Item " + r.nextInt(bound));
    }

    private void createTileMap() {
        final GridPaneFactory factory = (minMaxRows, minMaxCols) -> {
            final GridPane pane = new GridPane();
            pane.getColumnConstraints().clear();
            pane.getRowConstraints().clear();

            while (pane.getColumnCount() < minMaxCols) {
                pane.getColumnConstraints().add(new ColumnConstraints());
            }

            while (pane.getRowCount() < minMaxRows) {
                pane.getRowConstraints().add(new RowConstraints());
            }

            return pane;
        };

        final GameMap map = new GameMap(8, 12, factory);

        GrassSprite[] sprites = GrassSprite.values();
        Random r = new Random();

        for (int row = 0; row < 8; ++row) {
            for (int col = 0; col < 12; ++col) {
                final Grass tile = new Grass(
                        sprites[r.nextInt(sprites.length)],
                        row,
                        col
                );

                map.setNodeAt(row, col, tile.getFXSpriteView());
            }
        }
        map.displayIn(gameBackground);

        // Forcing scroll pane sizes
        final ScrollPane sp =
                (ScrollPane) this.gameBackground.getParent().getParent().getParent().getParent();
        // +2 allows us to get minimum size to remove the scroll bars
        sp.setMaxHeight((64 * 8) + 2);
        sp.setMaxWidth((64 * 12) + 2);
        sp.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        sp.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        this.map = map;
    }
}
