package gui;

import gui.itemview.ItemViewController;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
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
 * Main Game Window Controller; This would implement the 'RatGameActionListener' which would be the bridge
 * required to get events from the game to the GUI.
 *
 * @version 0.1
 * @author -Ry
 * Copyright: N/A
 */
public class GameSceneController implements Initializable {

    /**
     * Hardcode the Scene Object Hierarchy Resource to the Controller so that it can be accessed.
     */
    public static URL SCENE_FXML = GameSceneController.class.getResource("GameScene.fxml");

    private String styleSheet;

    /**
     * We don't know of these which are required yet; I can only assert that we will want:
     * <ol>
     *     <li><b>mainPane</b></li>
     *     <li><b>itemVbox</b></li>
     *     <li><b>gameBackgroundPane</b></li>
     *     <li><b>gameForegroundPane</b></li>
     * </ol>
     */

    @FXML
    private BorderPane mainPane;
    @FXML
    private HBox topSectionHbox;
    @FXML
    private HBox innerSectionHbox;
    @FXML
    private ScrollPane gameScrollPane;
    @FXML
    private ScrollPane itemScrollPane;
    @FXML
    private VBox itemVbox;

    /**
     * Background pane asserts the Size of the StackPane and also the Size of the Foreground; I.e.,
     * Size of background  == Size of foreground
     *                     == Size of StackPane
     *
     * Thus size is based on the map.
     */
    @FXML
    private Pane backgroundPane;
    @FXML
    private Pane foregroundPane;

    // This would actually be a HashMap of the format <Class<? extends Item>, ItemViewController>
    private List<ItemViewController> items;

    /**
     * @param url FXML File used to load this controller.
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
            throw new IllegalStateException("Multiple StyleSheets are not supported for: "
                    + getClass().getSimpleName()
            );
        }

        // This will be removed; it's just to showcase how adding an item will work.
        final Random r = new Random();
        items = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            final FXMLLoader loader = new FXMLLoader(ItemViewController.SCENE_FXML);
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

        // We probably won't use Canvas for this but this just showcases the design
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

        // Force scene sizes; note that forcing the primary stage window size doesn't really work here. We can leave if
        // for now, but note that the size of the window does not correlate entirely with the size of the game.
        backgroundPane.getChildren().add(c);
        backgroundPane.setPrefSize(totalWidth, totalHeight);
        gameScrollPane.setMaxSize(totalWidth, totalHeight);

        // Generate a grid
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                context.setFill(Color.color(Math.random(),Math.random(),Math.random()));
                context.fillRect(px * col, px * row, width, height);
            }
        }
    }

    public void setStyleSheet(String styleSheet) {
        Objects.requireNonNull(styleSheet);
        this.styleSheet = styleSheet;
        this.mainPane.getStylesheets().clear();
        this.mainPane.getStylesheets().add(styleSheet);
        this.items.forEach(i -> i.setStyleSheet(styleSheet));
    }
}
