package gui.editor.module.tab.entities.view.drag;

import game.classinfo.entity.EntityInfo;
import game.entity.Entity;
import gui.editor.module.dependant.CustomEventDataMap;
import gui.editor.module.tab.entities.EntitiesTab;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;

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

    public static final String DRAG_DROP_EVENT_ID
            = "[ENTITY-VIEW-EVENT] :: DRAG-DROP";

    private EntitiesTab container;
    private Parent root;
    private EntityInfo<?> target;

    @FXML
    private ImageView entityDisplayView;
    @FXML
    private Label entityNameLabel;

    public static EntityView init(final EntityInfo<?> target,
                                  final EntitiesTab tab) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final EntityView view = loader.getController();

            view.root = root;
            view.setTarget(target);
            view.container = tab;

            return view;

            // Rethrow
        } catch (final IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Event handlers
    ///////////////////////////////////////////////////////////////////////////

    @FXML
    private void onDragDetected(final MouseEvent e) {
        final Dragboard db = this.entityDisplayView.startDragAndDrop(
                TransferMode.ANY
        );
        db.setDragView(this.entityDisplayView.getImage());

        final CustomEventDataMap data = new CustomEventDataMap(
                EntityView.DRAG_DROP_EVENT_ID,
                this.toString()
        );
        db.setContent(data);

        e.consume();
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

    /**
     * Constructs the target entity of this view.
     *
     * @param row The row position for the entity.
     * @param col The column position for the entity.
     * @return Newly constructed entity.
     */
    public Entity newInstance(final int row,
                              final int col) {
        return this.target.constructEntity(row, col);
    }
}
