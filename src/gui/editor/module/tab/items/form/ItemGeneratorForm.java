package gui.editor.module.tab.items.form;

import game.entity.Item;
import game.generator.ItemGenerator;
import game.generator.loader.ItemGeneratorLoader;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.stage.Stage;
import util.SceneUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Java class created on 17/02/2022 for usage in project RatGame-A2. Form
 * allows the editing and construction of an {@link ItemGenerator}.
 *
 * @author -Ry
 * @version 0.1
 * Copyright: N/A
 */
public class ItemGeneratorForm implements Initializable {

    /**
     * Scene fxml resource.
     */
    private static final URL SCENE_FXML
            = ItemGeneratorForm.class.getResource("ItemGeneratorForm.fxml");

    ///////////////////////////////////////////////////////////////////////////
    // FXML Attributes
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Choice-box that consists of all the target item generators.
     */
    @FXML
    private ChoiceBox<ItemGeneratorLoader.GeneratorTarget> itemClassChoiceBox;

    /**
     * Current number of usages text field.
     */
    @FXML
    private TextField curUsagesText;

    /**
     * Max usages text field.
     */
    @FXML
    private TextField maxUsagesText;

    /**
     * Current refresh time text field.
     */
    @FXML
    private TextField curRefreshTimeText;

    /**
     * Max refresh time text field.
     */
    @FXML
    private TextField maxRefreshTimeText;

    ///////////////////////////////////////////////////////////////////////////
    // Class attributes
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Parent/root node of the scene.
     */
    private Parent root;

    /**
     * Stage that this form will be displayed on.
     */
    private Stage displayStage;

    /**
     * Has the form terminated naturally in that the user has clicked the
     * button to initiate the termination process.
     */
    private boolean isNaturalExit = false;

    ///////////////////////////////////////////////////////////////////////////
    // Static construction mechanisms
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Static construction mechanism that loads this form into the target stage.
     *
     * @param s The stage to load into.
     * @return Newly constructed instance.
     */
    public static ItemGeneratorForm init(final Stage s) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final ItemGeneratorForm form = loader.getController();

            form.root = root;
            form.displayStage = s;
            s.setScene(new Scene(root));

            return form;

            // Rethrow
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Event handlers
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Completes the scene if the scene is safe to complete. Closing the
     * window that the scene is being displayed in.
     */
    @FXML
    private void onBuildGeneratorClicked() {
        if (this.isComplete()) {
            this.isNaturalExit = true;
            this.displayStage.close();

            // Error message
        } else {
            final Alert ae = new Alert(Alert.AlertType.WARNING);
            ae.setHeaderText("Form Incomplete!!!");
            ae.setContentText("Some form data has not been supplied and is "
                    + "thus not able to compile.");
            ae.showAndWait();
        }
    }

    /**
     * Initialises the default values into the scene.
     *
     * @param url    Unused.
     * @param bundle Unused.
     */
    @Override
    public void initialize(final URL url,
                           final ResourceBundle bundle) {
        for (ItemGeneratorLoader.GeneratorTarget v
                : ItemGeneratorLoader.GeneratorTarget.values()) {
            this.itemClassChoiceBox.getItems().add(v);
        }

        final Supplier<TextFormatter<?>> v = () -> {
            return new TextFormatter<>(SceneUtil.POSITIVE_INTEGER_FORMAT);
        };

        // Text formatters only allow 1 -> INT_MAX - 1
        this.curRefreshTimeText.setTextFormatter(v.get());
        this.curUsagesText.setTextFormatter(v.get());
        this.maxRefreshTimeText.setTextFormatter(v.get());
        this.maxUsagesText.setTextFormatter(v.get());
    }

    /**
     * Sets the target item class to the provided item.
     *
     * @param clazz The target item class.
     */
    public void setItemClass(final Class<? extends Item> clazz) {
        for (ItemGeneratorLoader.GeneratorTarget t
                : ItemGeneratorLoader.GeneratorTarget.values()) {
            if (t.getTarget().equals(clazz)) {
                this.itemClassChoiceBox.getSelectionModel().select(t);
            }
        }
    }

    /**
     * Set the current number of usages text to the provided usages.
     *
     * @param cur Current number of usages.
     */
    public void setCurUsagesText(final int cur) {
        this.curUsagesText.setText(String.valueOf(cur));
    }

    /**
     * Set the maximum number of usages text.
     *
     * @param max The maximum number of usages.
     */
    public void setMaxUsagesText(final int max) {
        this.maxUsagesText.setText(String.valueOf(max));
    }

    /**
     * Set the current refresh time text.
     *
     * @param cur The current refresh time.
     */
    public void setCurRefreshTimeText(final int cur) {
        this.curRefreshTimeText.setText(String.valueOf(cur));
    }

    /**
     * Set the maximum refresh time text.
     *
     * @param max The maximum refresh time.
     */
    public void setMaxRefreshTimeText(final int max) {
        this.maxRefreshTimeText.setText(String.valueOf(max));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Data collection methods (get/set)
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Checks to see if the current state of the form is complete in that all
     * data fields are parseable.
     *
     * @return {@code true} if it is possible to parse all the data fields
     * into the target types.
     */
    public boolean isComplete() {
        // Check data validity
        final Function<TextField, Boolean> fn = (t) -> !t.getText().equals("");
        return itemClassChoiceBox.getValue() != null
                && fn.apply(curUsagesText)
                && fn.apply(curRefreshTimeText)
                && fn.apply(maxUsagesText)
                && fn.apply(maxRefreshTimeText);
    }

    /**
     * @return Root node of the scene.
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * @return Stage that this form is being displayed in.
     */
    public Stage getDisplayStage() {
        return displayStage;
    }

    /**
     * @return {@code true} if the form has terminated naturally.
     */
    public boolean isNaturalExit() {
        return isNaturalExit;
    }

    /**
     * Parses all the data fields and builds it into the target Generator type.
     *
     * @return Optional which can contain a new instance of an Item generator
     * using the data fields as the build parameters. However, if the form is
     * not complete or if some error occurs whilst parsing the data then an
     * empty optional is returned instead.
     */
    public Optional<ItemGenerator<?>> createGenerator() {

        // We only allow positive integers
        final Function<TextField, Integer> fn = (t) -> {
            try {
                return Integer.parseInt(t.getText());

                // The -1 may be unnecessary
            } catch (final Exception e) {
                return Integer.MAX_VALUE - 1;
            }
        };

        if (isComplete()) {
            try {
                final ItemGenerator<?> gen = this.itemClassChoiceBox
                        .getSelectionModel()
                        .getSelectedItem()
                        .create(
                                fn.apply(this.maxRefreshTimeText),
                                fn.apply(this.curRefreshTimeText),
                                fn.apply(this.curUsagesText),
                                fn.apply(this.maxUsagesText)
                        );

                return Optional.of(gen);

                // This can happen if we somehow allowed an empty string to
                // get parsed.
            } catch (final Exception e) {
                e.printStackTrace();
                return Optional.empty();
            }

        } else {
            return Optional.empty();
        }
    }

    /**
     * Clears this form so that it can be reused.
     */
    public void clear() {
        this.isNaturalExit = false;
        this.maxRefreshTimeText.clear();
        this.curUsagesText.clear();
        this.maxUsagesText.clear();
        this.curRefreshTimeText.clear();
    }
}
