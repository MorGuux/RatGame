package gui.editor.module.tab.items.view;

import game.generator.ItemGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Java class created on 17/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class ItemGeneratorEditor {

    private static final URL SCENE_FXML =
            ItemGeneratorEditor.class.getResource("ItemGeneratorEditor.fxml");

    ///////////////////////////////////////////////////////////////////////////
    // FXML Attributes
    ///////////////////////////////////////////////////////////////////////////

    @FXML
    private ImageView displaySprite;
    @FXML
    private Label targetEntityLabel;
    @FXML
    private Label curUsagesMaxUsagesLabel;
    @FXML
    private Label curTimeMaxTimeLabel;

    ///////////////////////////////////////////////////////////////////////////
    // Class variables
    ///////////////////////////////////////////////////////////////////////////

    private Parent root;
    private ItemGenerator<?> generator;
    private Consumer<ItemGeneratorEditor> onDeleteActionHandle;

    ///////////////////////////////////////////////////////////////////////////
    // Static construction mechanisms
    ///////////////////////////////////////////////////////////////////////////

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

    @FXML
    private void onEditClicked() {

    }

    @FXML
    private void onDeleteClicked() {
        if (this.onDeleteActionHandle != null) {
            this.onDeleteActionHandle.accept(this);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Data probing and mutation methods
    ///////////////////////////////////////////////////////////////////////////

    public Parent getRoot() {
        return root;
    }

    public ItemGenerator<?> getGenerator() {
        return generator;
    }

    public void setOnDeleteActionHandle(final Consumer<ItemGeneratorEditor> e) {
        this.onDeleteActionHandle = e;
    }

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

    @Override
    public int hashCode() {
        return Objects.hash(getRoot());
    }
}
