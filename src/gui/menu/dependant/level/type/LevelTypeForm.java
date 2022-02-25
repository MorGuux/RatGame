package gui.menu.dependant.level.type;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.UnaryOperator;

/**
 * Java class created on 11/02/2022 for usage in project RatGame-A2.
 * Represents a data input form so that one can obtain a username and a level
 * selection type or just a level type selection.
 *
 * @author -Ry, Shashank
 * @version 0.3
 * Copyright: N/A
 */
public class LevelTypeForm implements Initializable {

    /**
     * Scene fxml resource.
     */
    private static final URL SCENE_FXML
            = LevelTypeForm.class.getResource("LevelTypeForm.fxml");

    /**
     * Text input field which only allows a-z, A-Z, 0-9.
     */
    @FXML
    private TextField usernameInputField;

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
     * Static construction mechanism for initialising the form.
     *
     * @param s The stage to inject the scene info into.
     * @return A newly instantiated, setup, controller.
     */
    public static LevelTypeForm initScene(final Stage s) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final LevelTypeForm form = loader.getController();

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

    /**
     * Initialises the form assuming that the username is already known and
     * that only a Level type is desired.
     *
     * @param s The stage to show this form in.
     * @param username The already known username.
     * @return Newly constructed and setup form.
     */
    public static LevelTypeForm init(final Stage s,
                                     final String username) {
        final LevelTypeForm form = initScene(s);
        form.usernameInputField.setDisable(true);
        form.usernameInputField.setText(username);

        return form;
    }

    /**
     * Sets the custom level state and closes the scene.
     */
    @FXML
    private void onCustomLevelClicked() {
        if (getUsername().isPresent()) {
            this.isCustomLevel = true;
            this.displayStage.close();
        } else {
            errUsernameIncompleteAlert();
        }
    }

    /**
     * Sets the custom level state and closes the scene.
     */
    @FXML
    private void onDefaultLevelClicked() {
        if (getUsername().isPresent()) {
            this.isCustomLevel = false;
            this.displayStage.close();
        } else {
            errUsernameIncompleteAlert();
        }
    }

    /**
     * Displays an alert for when the username text field is incomplete.
     */
    private void errUsernameIncompleteAlert() {
        final Alert ae = new Alert(Alert.AlertType.INFORMATION);
        ae.setHeaderText("Please type a username!");
        ae.setContentText("The username provided is not complete.");
        ae.showAndWait();
    }

    /**
     * Initialises the base text input field.
     *
     * @param url    Un-used.
     * @param bundle Un-used.
     */
    @Override
    public void initialize(final URL url,
                           final ResourceBundle bundle) {

        // Forces that the username input field only have a to z; 0 to 9
        final UnaryOperator<TextFormatter.Change> handle = change -> {
            if (change.getControlNewText().matches("[0-9a-zA-Z ]*")) {
                return change;
            } else {
                return null;
            }
        };
        this.usernameInputField.setTextFormatter(new TextFormatter<>(handle));

    }

    /**
     * @return This scene object roots hierarchy.
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * @return Optional of the username that this form currently has. If the
     * optional is empty then the text was the empty string. Anything else
     * implies a char sequence of "a-z A-Z 0-9".
     */
    public Optional<String> getUsername() {
        final String s = this.usernameInputField.getText();

        if (s == null || s.equals("")) {
            return Optional.empty();
        } else {
            return Optional.of(s);
        }
    }

    /**
     * @return Optional of this forms current level selection state. If the
     * optional is empty it means that the form is not complete. If it is
     * present, {@code true} implies that a custom level is wanted.
     */
    public Optional<Boolean> getIsCustomLevel() {
        if (this.isCustomLevel == null) {
            return Optional.empty();
        } else {
            return Optional.of(this.isCustomLevel);
        }
    }
}
