package launcher;

import game.entity.subclass.rat.Rat;
import gui.game.GameSceneController;
import gui.menu.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Generic JavaFX launcher class.
 */
public class Main extends Application {

    /**
     * Main stage for this application.
     */
    private static Stage primaryStage;

    /**
     * Main scene for this application. This scene is purely our Main Menu
     * and should be kept loaded at all times.
     */
    private static Scene primaryScene;

    /**
     * Previous scene, even if the previous is the current scene.
     * <p>
     * Perhaps a temporary variable as we may not always need this.
     */
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
     * Sets the Primary Stage to the Main Menu of the Application.
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
