package launcher;

import gui.GameSceneController;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

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

    /**
     * Default main JavaFX launcher.
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

        // This wouldn't actually be our main/first scene; It would be the main menu here,
        // and the main menu would load the scene we are currently loading.
        final FXMLLoader loader = new FXMLLoader(GameSceneController.SCENE_FXML);
        final Parent root = loader.load();
        primaryStage.setScene(new Scene(root));

        // We may still want access to the loader so if you implement a static initialisation method
        // you should return the Loader
        primaryStage.show();
    }

    public static double getWindowWidth() {
        return primaryStage.getWidth();
    }

    public static double getWindowHeight() {
        return primaryStage.getHeight();
    }


    public static void setMaxSize(final double width,
                                  final double height) {
        Main.primaryStage.setMaxWidth(width);
        Main.primaryStage.setMaxHeight(height);
    }
}