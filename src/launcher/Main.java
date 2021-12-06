package launcher;

import gui.about.AboutSectionController;
import gui.assets.css.SceneStyle;
import gui.leaderboard.LeaderboardController;
import gui.menu.MainMenuController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;

/**
 * JavaFX Launcher class with added methods to allows modification of the
 * Main stage.
 *
 * @author Group 29
 * @version 0.2
 * Copyright: N/A
 */
public class Main extends Application {

    /**
     * Main stage for this application.
     */
    private static Stage mainStage;

    /**
     * Main scene for this application. This scene is purely our Main Menu
     * and should be kept loaded at all times.
     */
    private static Scene mainMenu;

    /**
     * Iterator of the resources which can be loaded through {@link SceneStyle}.
     */
    private static Iterator<SceneStyle> themeIterator;

    /**
     * Current theme used in for the Scene. Null theme is Default theme
     * {@link SceneStyle#DARK_THEME}; anything other than Null refers to any,
     * even the Default theme from {@link SceneStyle}.
     */
    private static String currentStyleSheet;

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

        Main.mainStage = primaryStage;
        final FXMLLoader main = loadMainMenu();

        final Scene sc = new Scene(main.load());
        mainMenu = sc;

        // Not sure if we will ever use this.
        final MainMenuController c = main.getController();

        primaryStage.setScene(sc);
        primaryStage.show();
    }

    /**
     * Cycle through a Stylesheet resource for the application.
     *
     * @return Stylesheet resource.
     * @throws FileNotFoundException If the resource has not loaded correctly.
     */
    public static String cycleCssTheme() throws FileNotFoundException {
        if (themeIterator == null
                || !themeIterator.hasNext()) {
            themeIterator = Arrays.stream(SceneStyle.values()).iterator();
        }

        // Definitely not a good piece of code. But this would only ever
        // error out if someone damages the css files (deletes/moves)
        assert themeIterator.hasNext();
        final SceneStyle sceneStyle = themeIterator.next();

        if (sceneStyle.getResource() == null) {
            throw new FileNotFoundException(
                    "Failed to load Style: "
                            + sceneStyle.name()
            );
        } else {
            return sceneStyle.getResource().toExternalForm();
        }
    }

    /**
     * Initialises an FXMLoader attached to the Main Menu scene.
     *
     * @return Initialised fxml loader.
     * @throws NullPointerException If the Scene FXML hierarchy file is
     *                              {@code null}.
     */
    private static FXMLLoader loadMainMenu() {
        Objects.requireNonNull(MainMenuController.SCENE_FXML);
        return new FXMLLoader(MainMenuController.SCENE_FXML);
    }

    /**
     *
     * @return
     */
    public static FXMLLoader loadLeaderboardStage() {
        Objects.requireNonNull(LeaderboardController.SCENE_FXML);
        return new FXMLLoader(LeaderboardController.SCENE_FXML);
    }

    /**
     * Initialises an FXMLoader attached to the About Section scene.
     * @return
     */
    public static FXMLLoader loadAboutSectionStage() {
        Objects.requireNonNull(AboutSectionController.SCENE_FXML);
        return new FXMLLoader(AboutSectionController.SCENE_FXML);
    }

    /**
     * Loads a new scene to the primary stage.
     *
     * @param scene The new scene to load.
     */
    public static void loadNewScene(final Scene scene) {
        mainStage.setScene(scene);
    }

    /**
     * Loads over the current scene the main menu. This does not store the
     * previous scene meaning there is no way back.
     */
    public static void reloadMainMenu() {
        mainStage.setScene(mainMenu);
    }

    /**
     * Override the main scenes' stylesheet to the provided stylesheet.
     *
     * @param sheet New stylesheet to use.
     */
    public static void setStyleSheet(final String sheet) {
        mainStage.getScene().getRoot().getStylesheets().clear();
        mainStage.getScene().getRoot().getStylesheets().add(sheet);
        currentStyleSheet = sheet;
    }

    /**
     * @return Current application stylesheet. Default value is the Dark
     * Theme stylesheet.
     */
    public static String getCurrentStyle() {
        if (currentStyleSheet == null) {
            return SceneStyle.DARK_THEME.getResource().toExternalForm();
        } else {
            return currentStyleSheet;
        }
    }
}
