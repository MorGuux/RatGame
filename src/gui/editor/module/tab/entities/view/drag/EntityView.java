package gui.editor.module.tab.entities.view.drag;

import game.classinfo.entity.EntityInfo;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

/**
 * Java class created on 13/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class EntityView {

    private static final URL SCENE_FXML
            = EntityView.class.getResource("EntityView.fxml");

    private Parent root;
    private EntityInfo<?> target;

    @FXML
    private ImageView entityDisplayView;

    @FXML
    private Label entityNameLabel;

    public static EntityView init(final EntityInfo<?> target) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final EntityView view = loader.getController();

            view.root = root;
            view.setTarget(target);

            return view;

            // Rethrow
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Event handlers
    ///////////////////////////////////////////////////////////////////////////

    @FXML
    private void onDragDetected(final MouseEvent e) {
        // todo
    }

    @FXML
    private void onInstanceBuilderClicked() {
        // todo
    }

    ///////////////////////////////////////////////////////////////////////////
    // Data set and collection methods
    ///////////////////////////////////////////////////////////////////////////

    public Parent getRoot() {
        return root;
    }

    private void setTarget(final EntityInfo<?> target) {
        this.target = target;

        // Update display sprite
        if (this.target.getDisplaySprite() != null) {
            this.entityDisplayView.setImage(new Image(
                    this.target.getDisplaySprite().toExternalForm()
            ));
        }

        // Update label
        this.entityNameLabel.setText(
                this.target.getTargetClass().getSimpleName()
        );
    }

    public EntityInfo<?> getTarget() {
        return target;
    }

    /**
     * @return Formatted string.
     */
    @Override
    public String toString() {
        return "[ENTITY-VIEW] :: "
                + getTarget().getTargetClass().getName();
    }
}
