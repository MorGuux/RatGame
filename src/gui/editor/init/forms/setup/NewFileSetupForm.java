package gui.editor.init.forms.setup;

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
 *
 * @author -Ry
 */
public class NewFileSetupForm implements Initializable {

    private static final URL SCENE_FXML
            = NewFileSetupForm.class.getResource("NewFileSetupForm.fxml");

    public static NewFileSetupForm init(final Stage s) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final NewFileSetupForm form = loader.getController();

            form.root = root;
            form.displayStage = s;
            s.setScene(new Scene(root));

            return form;
            // Stacktrace + rethrow
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }

    private static Optional<Integer> loadPositiveInteger(final String s) {
        try {
            return Optional.of(Integer.parseInt(s));

            // Will need to cap the actual value else where lol
        } catch (final NumberFormatException e) {
            return Optional.empty();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Class attributes
    ///////////////////////////////////////////////////////////////////////////

    @FXML
    private TextField friendlyNameField;
    @FXML
    private TextField rowCountField;
    @FXML
    private TextField columnCountField;
    @FXML
    private TextField timeLimitMsField;

    private Parent root;
    private Stage displayStage;

    ///////////////////////////////////////////////////////////////////////////
    // Event handles
    ///////////////////////////////////////////////////////////////////////////

    @FXML
    private void onContinueClicked() {
        if (getFriendlyName().isEmpty()
                || getRowCount().isEmpty()
                || getColumnCount().isEmpty()
                || getTimeLimit().isEmpty()) {
            final Alert ae = new Alert(Alert.AlertType.WARNING);
            ae.setHeaderText("Form incomplete!!!");
            ae.setContentText("The form is not currently in a complete field "
                    + "as there are one or more fields with invalid data.");
            ae.showAndWait();

            // Forms good (not really can still have Row > 99999) which is
            // not a good idea.
        } else {
            this.getDisplayStage().close();
        }
    }

    @Override
    public void initialize(final URL url,
                           final ResourceBundle bundle) {

        // Positive int handle
        final UnaryOperator<TextFormatter.Change> intHandle = (c) -> {
            if (c.getControlNewText().matches("[0-9]*")) {
                return c;
            } else {
                return null;
            }
        };

        // Text handle (File names specifically)
        final UnaryOperator<TextFormatter.Change> textHandle = (c) -> {
            if (c.getControlNewText().matches("[a-zA-Z0-9 _-]*")) {
                return c;
            } else {
                return null;
            }
        };

        // Set handles
        this.rowCountField.setTextFormatter(new TextFormatter<>(intHandle));
        this.columnCountField.setTextFormatter(new TextFormatter<>(intHandle));
        this.timeLimitMsField.setTextFormatter(new TextFormatter<>(intHandle));
        this.friendlyNameField.setTextFormatter(
                new TextFormatter<>(textHandle)
        );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Standard data collection/get methods
    ///////////////////////////////////////////////////////////////////////////

    public Optional<String> getFriendlyName() {
        if (this.friendlyNameField.getText().equals("")) {
            return Optional.empty();
        } else {
            return Optional.of(this.friendlyNameField.getText());
        }
    }

    public Optional<Integer> getRowCount() {
        if (this.rowCountField.getText().equals("")) {
            return Optional.empty();
        } else {
            return loadPositiveInteger(this.rowCountField.getText());
        }
    }

    public Optional<Integer> getColumnCount() {
        if (this.rowCountField.getText().equals("")) {
            return Optional.empty();
        } else {
            return loadPositiveInteger(this.columnCountField.getText());
        }
    }

    public Optional<Integer> getTimeLimit() {
        if (this.rowCountField.getText().equals("")) {
            return Optional.empty();
        } else {
            return loadPositiveInteger(this.timeLimitMsField.getText());
        }
    }

    public Parent getRoot() {
        return root;
    }

    public Stage getDisplayStage() {
        return displayStage;
    }
}