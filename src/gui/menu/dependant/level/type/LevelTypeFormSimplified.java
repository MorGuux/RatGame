package gui.menu.dependant.level.type;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class LevelTypeFormSimplified implements Initializable {

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

    private static String username;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public static LevelTypeFormSimplified initScene(final Stage s,
                                                    String selectedUsername) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);
        username = selectedUsername;
        try {
            final Parent root = loader.load();
            final LevelTypeFormSimplified form = loader.getController();

            final Scene scene = new Scene(root);
            s.setScene(scene);

            form.displayStage = s;
            form.root = root;

            return form;

            // Rethrow the exception
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }
    @FXML
    private void onDefaultLevelClicked() {
        this.isCustomLevel = false;
        this.displayStage.close();
    }
    @FXML
    private void onCustomLevelClicked() {
        this.isCustomLevel = true;
        this.displayStage.close();
    }
    public Optional<Boolean> getIsCustomLevel() {
        if (this.isCustomLevel == null) {
            return Optional.empty();
        } else {
            return Optional.of(this.isCustomLevel);
        }
    }
    public String getUsername() {
        return username;
    }
}
