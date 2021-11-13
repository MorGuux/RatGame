package gui.game;

import gui.game.dependant.itemview.ItemViewController;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import launcher.Main;

import javax.swing.*;
import javax.swing.Timer;
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
     * Stylesheet to use for the Node hierarchy.
     */
    private String styleSheet;

    /**
     * Delete once testing isn't needed anymore.
     */
    private List<ItemViewController> temporaryList;

    /**
     * Initialises the main scene.
     *
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

        // Test code
        temporaryList = new ArrayList<>();
        createItems();
        final Timer t = new Timer(1000, (e) -> {
            Platform.runLater(this::updateRandomItemData);
        });
        t.start();
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
        Main.setStyleSheet(styleSheet);
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
        final int bound = 8;
        for (int i = 0; i < 4; i++) {
            final FXMLLoader loader =
                    new FXMLLoader(ItemViewController.SCENE_FXML);
            try {
                final Parent p = loader.load();
                final ItemViewController c = loader.getController();
                final int max = r.nextInt(bound) + 1;

                c.setMaxUsages(max);
                c.setCurrentUsages(r.nextInt(max));

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
}
