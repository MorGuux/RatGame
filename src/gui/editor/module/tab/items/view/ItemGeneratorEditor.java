package gui.editor.module.tab.items.view;

import game.generator.ItemGenerator;
import gui.editor.module.tab.items.form.ItemGeneratorForm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Java class created on 17/02/2022 for usage in project RatGame-A2. Visual
 * representation of an Item Generator providing control options to edit the
 * underlying values held within the item generator.
 *
 * @author -Ry
 * @version 0.3
 * Copyright: N/A
 */
public class ItemGeneratorEditor {

    /**
     * Scene fxml resource.
     */
    private static final URL SCENE_FXML =
            ItemGeneratorEditor.class.getResource("ItemGeneratorEditor.fxml");

    ///////////////////////////////////////////////////////////////////////////
    // FXML Attributes
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Target item visual display.
     */
    @FXML
    private ImageView displaySprite;

    /**
     * Name label for the target entity.
     */
    @FXML
    private Label targetEntityLabel;

    /**
     * Label for the current number of usages and the maximum number.
     */
    @FXML
    private Label curUsagesMaxUsagesLabel;

    /**
     * Current time to max time label.
     */
    @FXML
    private Label curTimeMaxTimeLabel;

    ///////////////////////////////////////////////////////////////////////////
    // Class variables
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Root node of the scene.
     */
    private Parent root;

    /**
     * Item generator that this displays visual information about.
     */
    private ItemGenerator<?> generator;

    /**
     * Handle which is called when this generator should be deleted.
     */
    private Consumer<ItemGeneratorEditor> onDeleteActionHandle;

    ///////////////////////////////////////////////////////////////////////////
    // Static construction mechanisms
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Static construction mechanism for loading the fxml dependencies and
     * initialising the target.
     *
     * @param target The target item generator to display information about.
     * @return Newly constructed instances setup for the target.
     */
    public static ItemGeneratorEditor init(final ItemGenerator<?> target) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final ItemGeneratorEditor editor = loader.getController();

            editor.root = root;
            editor.setGenerator(target);
            return editor;

            // Rethrow
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Event handlers
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Sets the target generator to the provided generator updating any and
     * all inner states.
     *
     * @param gen The generator to set to.
     */
    private void setGenerator(final ItemGenerator<?> gen) {
        this.generator = gen;
        this.displaySprite.setImage(new Image(
                gen.getDisplaySprite().toExternalForm()
        ));
        this.targetEntityLabel.setText(
                gen.getItemClass().getSimpleName()
        );
        this.curTimeMaxTimeLabel.setText(String.format(
                "%s / %s",
                this.generator.getVariableTime(),
                this.generator.getRefreshTime()
        ));
        this.curUsagesMaxUsagesLabel.setText(String.format(
                "%s / %s",
                this.generator.getAvailableUsages(),
                this.generator.getMaximumUsages()
        ));
    }

    /**
     * Creates a Form and populates it with the data held within the target
     * generator and then lets the user modify and edit any parameter.
     */
    @FXML
    private void onEditClicked() {
        final Stage s = new Stage();
        s.initModality(Modality.APPLICATION_MODAL);
        final ItemGeneratorForm form = ItemGeneratorForm.init(s);

        // Fill form with current data
        form.setItemClass(this.generator.getItemClass());
        form.setCurUsagesText(this.generator.getAvailableUsages());
        form.setMaxUsagesText(this.generator.getMaximumUsages());
        form.setCurRefreshTimeText(this.generator.getVariableTime());
        form.setMaxRefreshTimeText(this.generator.getRefreshTime());

        // Show form
        s.showAndWait();

        // Set the generator to the new one
        if (form.isNaturalExit()) {
            form.createGenerator().ifPresent(this::setGenerator);
        }
    }

    /**
     * Deletes this generator from the parent.
     */
    @FXML
    private void onDeleteClicked() {
        if (this.onDeleteActionHandle != null) {
            this.onDeleteActionHandle.accept(this);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Data probing and mutation methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @return Root node of the scene.
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * @return The current target item generator instance.
     */
    public ItemGenerator<?> getGenerator() {
        return generator;
    }

    /**
     * Sets the delete action handle to the provided handle.
     *
     * @param e The action to perform when this view is being deleted.
     */
    public void setOnDeleteActionHandle(final Consumer<ItemGeneratorEditor> e) {
        this.onDeleteActionHandle = e;
    }

    /**
     * Checks for equality of this object and another object.
     *
     * @param o The other object.
     * @return {@code true} if the provided object is this, or its root is
     * equal to this. Else {@code false}.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ItemGeneratorEditor that)) {
            return false;
        } else {
            return getRoot().equals(that.getRoot());
        }
    }

    /**
     * @return Hashcode of the parent/root.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getRoot());
    }
}
