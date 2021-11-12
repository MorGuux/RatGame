package launcher;

import gui.game.GameSceneController;
import gui.menu.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Sample application that demonstrates the use of JavaFX Canvas for a Game.
 * This class is intentionally not structured very well. This is just a starting point to show
 * how to draw an image on a canvas, respond to arrow key presses, use a tick method that is
 * called periodically, and use drag and drop.
 * <p>
 * Do not build the whole application in one file. This file should probably remain very small.
 *
 * @author Liam O'Reilly
 */
public class Main extends Application {

    private static Stage primaryStage;

    private static Scene primaryScene;

    private static Scene previousScene;

    /**
     * Default main JavaFX launcher.
     *
     * @param args Mostly unused.
     */
    public static void main(final String[] args) {
        launch(args);
    }

    /**
     * Initialise main object hierarchy and load it.
     *
     * @param primaryStage Main stage to display onto.
     * @throws IOException If any occur during the FXML Loading process.
     */
    public void start(final Stage primaryStage) throws IOException {
        Main.primaryStage = primaryStage;
        final FXMLLoader main = loadMainMenu();

        final Scene sc = new Scene(main.load());
        primaryScene = sc;
        previousScene = sc;

        final MainMenuController c = main.getController();

        primaryStage.setScene(sc);
        primaryStage.show();
    }

    /**
     * Initialises an FXMLoader attached to the Main Menu scene.
     *
     * @return Initialised fxml loader.
     */
    private static FXMLLoader loadMainMenu() {
        return new FXMLLoader(MainMenuController.SCENE_FXML);
    }

    /**
     * Initialises an FXMLoader attached to the Game scene.
     *
     * @return Initialised fxml loader.
     */
    public static FXMLLoader loadGameStage() {
        return new FXMLLoader(GameSceneController.SCENE_FXML);
    }

    /**
     *
     */
    public static void loadMainScene() {
        primaryStage.setScene(primaryScene);
    }

    /**
     * Loads a new scene to the primary stage.
     *
     * @param scene The new scene to load.
     */
    public static void loadNewScene(final Scene scene) {
        previousScene = primaryStage.getScene();
        primaryStage.setScene(scene);
    }

    /**
     * Loads over the current scene, the previous scene.
     */
    public static void loadPreviousScene() {
        primaryStage.setScene(previousScene);
    }
}