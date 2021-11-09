package gui;

import gui.itemview.ItemViewController;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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

    // Scene container nodes
    @FXML
    private BorderPane mainPane;
    @FXML
    private HBox topSectionHbox;
    @FXML
    private HBox innerSectionHbox;
    @FXML
    private ScrollPane gameScrollPane;
    @FXML
    private AnchorPane gameAnchorPane;
    @FXML
    private ScrollPane itemScrollPane;
    @FXML
    private VBox itemVbox;

    // This would actually be a HashMap of the format <Class<? extends Item, ItemViewController>>
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

        // This just ensures that the anchor pane for the game is always bigger than the scroll pane;
        // since otherwise it would look ugly with patches when resizing.
        Platform.runLater(() -> {
            mainPane.getScene().widthProperty().addListener((observableValue, number, t1) -> {
                this.gameAnchorPane.setPrefWidth(t1.doubleValue());
            });
            mainPane.getScene().heightProperty().addListener((observableValue, number, t1) -> {
                this.gameAnchorPane.setPrefHeight(t1.doubleValue());
            });
        });
    }

    public void setStyleSheet(String styleSheet) {
        Objects.requireNonNull(styleSheet);
        this.styleSheet = styleSheet;
        this.mainPane.getStylesheets().clear();
        this.mainPane.getStylesheets().add(styleSheet);
        this.items.forEach(i -> i.setStyleSheet(styleSheet));
    }
}
