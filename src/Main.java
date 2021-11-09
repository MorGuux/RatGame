import gui.GameSceneController;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    // The dimensions of the window
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;

    // Timeline which will cause tick method to be called periodically.
    private Timeline tickTimeline;

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Setup the new application.
     *
     * @param primaryStage The stage that is to be used for the application.
     */
    public void start(Stage primaryStage) throws IOException {

        // Register a tick method to be called periodically.
        // Make a new timeline with one keyframe that triggers the tick method every half a second.
        tickTimeline = new Timeline(new KeyFrame(Duration.millis(500), event -> updateTick()));
        // Loop the timeline forever
        tickTimeline.setCycleCount(Animation.INDEFINITE);
        // We start the timeline upon a button press.

        // This wouldn't actually be our main/first scene; It would be the main menu here,
        // and the main, menu would load the scene we are loading.
        final FXMLLoader loader = new FXMLLoader(GameSceneController.SCENE_FXML);
        final Parent root = loader.load();
        primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));

        // We may want the loader to
        primaryStage.show();
    }

    /**
     * This method is called periodically by the tick timeline
     * and would for, example move, perform logic in the game,
     * this might cause the bad guys to move (by e.g., looping
     * over them all and calling their own tick method).
     */
    public void updateTick() {

    }
}