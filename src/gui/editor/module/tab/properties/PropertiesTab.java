package gui.editor.module.tab.properties;

import game.level.reader.module.GameProperties;
import gui.editor.init.LevelEditorBuilder;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import util.ChangeHandle;
import util.MinMaxPredicate;
import util.SceneUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Java class created on 13/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class PropertiesTab implements Initializable {

    // I didn't have to make this class so complicated it is just a bunch of
    // text fields, but I thought fuck it, why not.

    /**
     * Scene fxml resource.
     */
    private static final URL SCENE_FXML
            = PropertiesTab.class.getResource("PropertiesTab.fxml");

    /**
     * Text field consisting of the level name.
     */
    @FXML
    private TextField levelNameTextField;

    /**
     * Text field consisting of the number of rows.
     */
    @FXML
    private TextField numRowsTextField;

    /**
     * Text field consisting of the number of columns.
     */
    @FXML
    private TextField numColumnsTextField;

    /**
     * Text field consisting of the maximum number of rats.
     */
    @FXML
    private TextField maxRatsTextField;

    /**
     * Text field consisting of the time limit in MS.
     */
    @FXML
    private TextField timeLimitMsTextField;

    /**
     * Scene root node.
     */
    private Parent root;

    /**
     * Default game properties.
     */
    private GameProperties originalProperties;

    /**
     * Static construction mechanism.
     *
     * @return Newly constructed Properties tab.
     */
    public static PropertiesTab init() {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final PropertiesTab tab = loader.getController();
            tab.root = root;

            return tab;

            // Stack trace + rethrow
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Initialises the scene node objects.
     *
     * @param url    Un-used.
     * @param bundle Un-used.
     */
    @Override
    public void initialize(final URL url,
                           final ResourceBundle bundle) {
        this.levelNameTextField.setTextFormatter(new TextFormatter<>(
                SceneUtil.FILE_NAME_FORMAT
        ));

        // Set text formatter for integer fields
        this.numRowsTextField.setTextFormatter(new TextFormatter<>(
                SceneUtil.POSITIVE_INTEGER_FORMAT
        ));
        this.numColumnsTextField.setTextFormatter(new TextFormatter<>(
                SceneUtil.POSITIVE_INTEGER_FORMAT
        ));
        this.timeLimitMsTextField.setTextFormatter(new TextFormatter<>(
                SceneUtil.POSITIVE_INTEGER_FORMAT
        ));
        this.maxRatsTextField.setTextFormatter(new TextFormatter<>(
                SceneUtil.POSITIVE_INTEGER_FORMAT
        ));

        // Add tool tips
        final Consumer<TextField> toolTipInjector = (f) -> {
            final Tooltip tip = new Tooltip();
            tip.setText(f.getPromptText());
            tip.setShowDelay(Duration.ZERO);
            tip.setShowDuration(Duration.INDEFINITE);
            f.setTooltip(tip);
        };
        toolTipInjector.accept(this.levelNameTextField);
        toolTipInjector.accept(this.maxRatsTextField);
        toolTipInjector.accept(this.timeLimitMsTextField);
        toolTipInjector.accept(this.numRowsTextField);
        toolTipInjector.accept(this.numColumnsTextField);

        // Set text focus handle
        this.levelNameTextField.focusedProperty().addListener(
                new ChangeHandle<>(
                        this.levelNameTextField,
                        (s) -> !s.matches("\\s*"),
                        levelNameTextField::getText,
                        levelNameTextField::setText,
                        PropertiesTab.this::update
                ));

        // Set handle for min/max data fields
        final int min = 0;
        final int max = LevelEditorBuilder.MAXIMUM_GRID_SIZE;
        final MinMaxPredicate pred = new MinMaxPredicate(min, max);
        final BiConsumer<TextField, Predicate<String>> textHandle = (t, p) -> {
            t.focusedProperty().addListener(new ChangeHandle<>(
                    t,
                    p,
                    t::getText,
                    t::setText,
                    PropertiesTab.this::update
            ));
        };

        // Add range handle for data fields
        textHandle.accept(this.numRowsTextField, pred);
        textHandle.accept(this.numColumnsTextField, pred);
        textHandle.accept(
                this.timeLimitMsTextField,
                MinMaxPredicate.INT_SCOPED_PREDICATE
        );
        textHandle.accept(
                this.maxRatsTextField,
                MinMaxPredicate.INT_SCOPED_PREDICATE
        );
    }

    /**
     * @return Scene root node.
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * Sets the original game properties to the provided properties.
     *
     * @param properties The new 'default' properties.
     */
    public void setOriginalProperties(final GameProperties properties) {
        this.originalProperties = properties;
    }

    /**
     * Resets all data fields to the data held within the default game
     * properties.
     */
    public void resetToDefaults() {
        this.levelNameTextField.setText(
                originalProperties.getLevelName()
        );
        this.numRowsTextField.setText(String.valueOf(
                originalProperties.getRows()
        ));
        this.numColumnsTextField.setText(String.valueOf(
                originalProperties.getColumns()
        ));
        this.maxRatsTextField.setText(String.valueOf(
                originalProperties.getMaxRats()
        ));
        this.timeLimitMsTextField.setText(String.valueOf(
                originalProperties.getTimeLimit()
        ));
    }

    /**
     * Internal update function called whenever some text field has had new
     * committed data.
     *
     * @param target The target text field which has had committed data.
     */
    private void update(final TextField target) {
        System.out.printf(
                "Text field [%s] has had committed data!%n",
                target.getId()
        );
    }
}
