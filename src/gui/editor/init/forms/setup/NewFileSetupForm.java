package gui.editor.init.forms.setup;

import game.contextmap.ContextualMap;
import game.level.levels.template.TemplateEditor;
import game.level.levels.template.TemplateElement;
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
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.StringJoiner;
import java.util.function.UnaryOperator;

/**
 * Java class created on 11/02/2022 for usage in project RatGame-A2. Form
 * used to set up some file so that it is compatible with the Level editor
 * collecting and writing all the required data to the specified fields.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class NewFileSetupForm implements Initializable {

    /**
     * Scene fxml resource.
     */
    private static final URL SCENE_FXML
            = NewFileSetupForm.class.getResource("NewFileSetupForm.fxml");

    /**
     * Constructs the form and uses the provided stage as its target for
     * displaying its scene content.
     *
     * @param s The stage to display in.
     * @return Newly constructed form.
     */
    public static NewFileSetupForm init(final Stage s) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final NewFileSetupForm form = loader.getController();

            form.root = root;
            form.displayStage = s;
            s.setScene(new Scene(root));

            return form;

            // rethrow
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Helper function for loading a positive integer from the provided
     * string.
     *
     * @param s The string to load and parse.
     * @return Optional of an integer value where an empty optional indicates
     * that the value was not an integer, too large for an integer, or was
     * not a positive integer.
     */
    private static Optional<Integer> loadPositiveInteger(final String s) {
        try {
            final int v = Integer.parseInt(s);

            if (v <= 0) {
                return Optional.empty();
            } else {
                return Optional.of(v);
            }

            // You can't really make assumptions here; well not without
            // evaluating the string manually
        } catch (final NumberFormatException e) {
            return Optional.empty();
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Class attributes
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Friendly name text input field.
     */
    @FXML
    private TextField friendlyNameField;

    /**
     * Number of rows input field.
     */
    @FXML
    private TextField rowCountField;

    /**
     * Number of columns input field.
     */
    @FXML
    private TextField columnCountField;

    /**
     * Maximum number of rats input field.
     */
    @FXML
    private TextField maxRatsField;

    /**
     * Time limit in milliseconds text input field.
     */
    @FXML
    private TextField timeLimitMsField;

    /**
     * Root node of the scene.
     */
    private Parent root;

    /**
     * Stage that this form is being displayed in.
     */
    private Stage displayStage;

    /**
     * Has the form terminated through the natural means. By this I mean has
     * the user successfully initiated the scene close sequence.
     */
    private boolean isNaturalExit = false;

    ///////////////////////////////////////////////////////////////////////////
    // Event handles
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Attempts to initiate the form completion sequence, however if the data
     * fields don't pass a quick test then it is assumed that the form is not
     * ready and thus will not terminate.
     */
    @FXML
    private void onContinueClicked() {
        if (getFriendlyName().isEmpty()
                || getRowCount().isEmpty()
                || getColumnCount().isEmpty()
                || getMaxRats().isEmpty()
                || getTimeLimit().isEmpty()) {
            final Alert ae = new Alert(Alert.AlertType.WARNING);
            ae.setHeaderText("Form incomplete!!!");
            ae.setContentText("The form is not currently in a complete field "
                    + "as there are one or more fields with invalid data.");
            ae.showAndWait();

            // Forms good (not really can still have values > 99999) which is
            // not a good idea.
        } else {
            this.isNaturalExit = true;
            this.getDisplayStage().close();
        }
    }

    /**
     * Loads text field text handlers to force specific data types like
     * integer values.
     *
     * @param url    Unused.
     * @param bundle Unused.
     */
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
        this.maxRatsField.setTextFormatter(new TextFormatter<>(intHandle));
        this.friendlyNameField.setTextFormatter(
                new TextFormatter<>(textHandle)
        );
    }

    ///////////////////////////////////////////////////////////////////////////
    // Standard data collection/get methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @return Optional of the friendly name, if the friendly name exists.
     */
    public Optional<String> getFriendlyName() {
        if (this.friendlyNameField.getText().equals("")) {
            return Optional.empty();
        } else {
            return Optional.of(this.friendlyNameField.getText());
        }
    }

    /**
     * @return Optional of the number of rows.
     */
    public Optional<Integer> getRowCount() {
        if (this.rowCountField.getText().equals("")) {
            return Optional.empty();
        } else {
            return loadPositiveInteger(this.rowCountField.getText());
        }
    }

    /**
     * @return Optional of the number of rows.
     */
    public Optional<Integer> getColumnCount() {
        if (this.rowCountField.getText().equals("")) {
            return Optional.empty();
        } else {
            return loadPositiveInteger(this.columnCountField.getText());
        }
    }

    /**
     * @return Optional of the maximum number of rats.
     */
    public Optional<Integer> getMaxRats() {
        if (this.maxRatsField.getText().equals("")) {
            return Optional.empty();
        } else {
            return loadPositiveInteger(this.maxRatsField.getText());
        }
    }

    /**
     * @return Optional of the time limit in milliseconds.
     */
    public Optional<Integer> getTimeLimit() {
        if (this.rowCountField.getText().equals("")) {
            return Optional.empty();
        } else {
            return loadPositiveInteger(this.timeLimitMsField.getText());
        }
    }

    /**
     * @return Root node of this scene.
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * @return Display stage, that this form is being displayed in.
     */
    public Stage getDisplayStage() {
        return displayStage;
    }

    /**
     * @return {@code true} if the form has terminated naturally in the sense
     * that the user has successfully initiated the form completion sequence.
     * Else if not then {@code false} is returned.
     */
    public boolean isNaturalExit() {
        return isNaturalExit;
    }

    /**
     * Creates from the data held within this form, a template editor.
     *
     * @return Template editor with certain attributes set to what is held
     * within this form.
     * @throws IOException           If the template editor fails to load.
     * @throws IllegalStateException If {@link #isNaturalExit()} if false.
     */
    public TemplateEditor createEditor() throws IOException {
        if (!isNaturalExit) {
            throw new IllegalStateException(
                    "Form is currently incomplete and a Template editor "
                            + "cannot be created at this time."
            );

            // Create editor
        } else {
            final TemplateEditor editor = new TemplateEditor();

            editor.setElement(
                    TemplateElement.FRIENDLY_NAME,
                    friendlyNameField.getText()
            );
            editor.setElement(
                    TemplateElement.MAP_ROW_COUNT,
                    rowCountField.getText()
            );
            editor.setElement(
                    TemplateElement.MAP_COL_COUNT,
                    columnCountField.getText()
            );
            editor.setElement(
                    TemplateElement.TIME_LIMIT,
                    timeLimitMsField.getText()
            );
            editor.setElement(
                    TemplateElement.MAX_RATS,
                    maxRatsField.getText()
            );

            final int rows = getRowCount().orElse(0);
            final int cols = getColumnCount().orElse(0);
            final ContextualMap map = ContextualMap.emptyMap(rows, cols);

            // Compile map to one string
            final StringJoiner sj = new StringJoiner(System.lineSeparator());
            Arrays.stream(map.getTiles()).forEach(i -> {
                Arrays.stream(i).forEach(j -> {
                    sj.add(j.buildToString());
                });
            });

            editor.setElement(TemplateElement.MAP_LAYOUT, sj.toString());

            return editor;
        }
    }
}
