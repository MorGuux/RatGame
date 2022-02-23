package gui.menu.dependant.level.type;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Optional;

/**
 * Level type form which allows the selection of a target level type.
 *
 * @author -Ry, Shashank, Artem
 * @version 0.2
 * Copyright: N/A
 */
public class LevelTypeFormSimplified {

    /**
     * Scene fxml resource.
     */
    private static final URL SCENE_FXML
            = LevelTypeForm.class.getResource(
            "LevelTypeFormSimplified.fxml");

    /**
     * Root node of this scene.
     */
    private Parent root;

    /**
     * Boolean stating if the level selection is a custom level or not.
     */
    private Boolean isCustomLevel;

    /**
     * The stage showing this forms node hierarchy.
     */
    private Stage displayStage;

    /**
     * The username of the user.
     */
    private String username;

    /**
     * Static construction mechanism which loads the form and any fxml
     * dependencies alongside any construction parameters that are required.
     *
     * @param s        The stage to display the scene in.
     * @param username The default username of the user.
     * @return Newly constructed level type form.
     */
    public static LevelTypeFormSimplified initScene(final Stage s,
                                                    final String username) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);
        try {
            final Parent root = loader.load();
            final LevelTypeFormSimplified form = loader.getController();

            final Scene scene = new Scene(root);
            s.setScene(scene);

            form.displayStage = s;
            form.root = root;
            form.username = username;

            return form;

            // Rethrow the exception
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Default level selection action.
     */
    @FXML
    private void onDefaultLevelClicked() {
        this.isCustomLevel = false;
        this.displayStage.close();
    }

    /**
     * Custom level selection action.
     */
    @FXML
    private void onCustomLevelClicked() {
        this.isCustomLevel = true;
        this.displayStage.close();
    }

    /**
     * @return Optional of if the current state of the level type form is a
     * custom level. If the Optional is empty, then the user terminated the
     * scene without making a choice, if the optional has a value the true
     * indicates that a custom level is desired.
     */
    public Optional<Boolean> getIsCustomLevel() {
        if (this.isCustomLevel == null) {
            return Optional.empty();
        } else {
            return Optional.of(this.isCustomLevel);
        }
    }

    /**
     * @return Root node of this scene.
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * @return The stage that the form is being displayed in.
     */
    public Stage getDisplayStage() {
        return displayStage;
    }

    /**
     * @return The default username provided at construction.
     */
    public String getUsername() {
        return username;
    }
}
