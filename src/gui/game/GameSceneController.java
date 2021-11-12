package gui.game;

import gui.game.dependant.entitymap.NodeGridMap;
import gui.game.dependant.itemview.ItemViewController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import launcher.Main;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Main Game Window Controller; This would implement the 'RatGameActionListener'
 * which would be the bridge required to get events from the game to the GUI.
 *
 * @author -Ry
 * Copyright: N/A
 * @version 0.1
 */
public class GameSceneController implements Initializable {

    /**
     * Hardcode the Scene Object Hierarchy Resource to the Controller so that
     * it can be accessed.
     */
    public static final URL SCENE_FXML =
            GameSceneController.class.getResource("GameScene.fxml");

    /**
     * Stylesheet to use for the Node hierarchy.
     */
    private String styleSheet;

    /**
     * We don't know of these which are required yet. I can only assert that we
     * will want:
     * <ol>
     *     <li><b>mainPane</b></li>
     *     <li><b>itemVbox</b></li>
     *     <li><b>gameBackgroundPane</b></li>
     *     <li><b>gameForegroundPane</b></li>
     * </ol>
     */

    @FXML
    private BorderPane mainPane;

    // todo clean up this Class
    @SuppressWarnings("all")
    public HBox topSectionHbox;
    @FXML
    private HBox innerSectionHbox;
    @FXML
    private ScrollPane gameScrollPane;
    @FXML
    private ScrollPane itemScrollPane;
    @FXML
    private VBox itemVbox;

    /**
     * Background pane asserts the Size of the StackPane and also the Size of
     * the Foreground; I.e., Size of background  == Size of foreground ==
     * Size of StackPane.
     * <p>
     * Thus size is based on the map.
     */
    @FXML
    private Pane backgroundPane;
    @FXML
    private Pane foregroundPane;

    // This would actually be a HashMap of the format <Class<? extends Item>,
    // ItemViewController>
    private List<ItemViewController> items;

    private NodeGridMap<Long, ImageView> entityMap;

    /**
     * @param url            FXML File used to load this controller.
     * @param resourceBundle Not sure, but should be null in our case.
     */
    @Override
    public void initialize(final URL url,
                           final ResourceBundle resourceBundle) {
        // Enforce only a single style sheet
        final List<String> styles = mainPane.getStylesheets();
        if (styles.size() == 1) {
            this.styleSheet = styles.get(0);
        } else {
            throw new IllegalStateException(
                    "Multiple StyleSheets are not supported for: "
                            + getClass().getSimpleName()
            );
        }

        // todo everything below to the } is just test code
        // This will be removed; it's just to showcase how adding an item
        // will work.
        final Random r = new Random();
        items = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            final FXMLLoader loader
                    = new FXMLLoader(ItemViewController.SCENE_FXML);
            try {
                final Parent root = loader.load();
                itemVbox.getChildren().add(root);

                final ItemViewController c = loader.getController();
                c.setStyleSheet(this.styleSheet);
                c.setUsagesValue(r.nextInt(1000));
                items.add(c);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // We probably won't use Canvas for this but this just showcases the
        // design
        final int width = 48;
        final int height = 48;
        final int px = 48;
        final int rows = 20;
        final int cols = 30;
        final int totalWidth = width * cols;
        final int totalHeight = height * rows;

        // Force the size of the canvas
        final Canvas c = new Canvas(totalWidth, totalHeight);
        final GraphicsContext context = c.getGraphicsContext2D();

        // Force scene sizes; note that forcing the primary stage window size
        // doesn't really work here. We can leave if for now, but note that
        // the size of the window does not correlate entirely with the size
        // of the game.
        backgroundPane.getChildren().add(c);
        backgroundPane.setPrefSize(totalWidth, totalHeight);
        gameScrollPane.setMaxSize(totalWidth, totalHeight);

        // Generate a grid
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                context.setFill(Color.color(
                        Math.random(),
                        Math.random(),
                        Math.random()
                ));
                context.fillRect(px * col, px * row, width, height);
            }
        }

        // - - - ENTITY MAP - - -
        entityMap = new NodeGridMap<>(foregroundPane, width);
        final ImageView test = new ImageView(
                new Image("gui/assets/item_placeholder.jpg")
        );
        test.setFitHeight(32);
        test.setFitHeight(32);
        entityMap.trackNode(0L, test, 32, 0, 0);
    }

    /**
     * Sets the stylesheet for the top of the Node hierarchy
     * for this Scene.
     *
     * @param styleSheet Stylesheet to use.
     */
    public void setStyleSheet(final String styleSheet) {
        Objects.requireNonNull(styleSheet);
        this.styleSheet = styleSheet;
        this.mainPane.getStylesheets().clear();
        this.mainPane.getStylesheets().add(styleSheet);
        this.items.forEach(i -> i.setStyleSheet(styleSheet));
    }

    // todo delete once testing is no longer needed
    public void moveNode(MouseEvent event) {
        final List<List<Integer>> path = new ArrayList<>();
        path.add(List.of(0, 1));
        path.add(List.of(0, 2));
        path.add(List.of(0, 3));
        path.add(List.of(1, 3));
        path.add(List.of(2, 3));
        path.add(List.of(3, 3));
        final Runnable r = () -> {
            for (List<Integer> p : path) {
                int x = p.get(0);
                int y = p.get(1);
                entityMap.setNodePosition(0L, x, y);
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        new Thread(r).start();
    }

    public void loadPreviousScene(MouseEvent event) {
        Main.loadPreviousScene();
    }
}
