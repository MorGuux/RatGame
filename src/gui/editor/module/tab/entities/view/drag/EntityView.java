package gui.editor.module.tab.entities.view.drag;

import game.classinfo.entity.EntityInfo;
import game.entity.Entity;
import gui.editor.module.dependant.CustomEventDataMap;
import gui.editor.module.tab.entities.EntitiesTab;
import gui.type.TypeConstructionForm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

/**
 * Java class created on 13/02/2022 for usage in project RatGame-A2. View
 * that visualises some entity and its data.
 *
 * @author -Ry
 * @version 0.3
 * Copyright: N/A
 */
public class EntityView {

    /**
     * Scene fxml resource.
     */
    private static final URL SCENE_FXML
            = EntityView.class.getResource("EntityView.fxml");

    /**
     * Event ID string.
     */
    public static final String DRAG_DROP_EVENT_ID
            = "[ENTITY-VIEW-EVENT] :: DRAG-DROP";

    /**
     * Container which this view is a member of.
     */
    private EntitiesTab container;

    /**
     * Root node of this scene.
     */
    private Parent root;

    /**
     * Reflection level data collection methods for the target entity.
     */
    private EntityInfo<?> target;

    /**
     * Entity display image view consisting of this entities display sprite.
     */
    @FXML
    private ImageView entityDisplayView;

    /**
     * Entity name label which consists of the name of the target entity.
     */
    @FXML
    private Label entityNameLabel;


    /**
     * Static construction mechanism for constructing a view of a target entity.
     *
     * @param target The entity to create a view for.
     * @param tab    The tab to act as this views parent.
     * @return Newly constructed view.
     */
    public static EntityView init(final EntityInfo<?> target,
                                  final EntitiesTab tab) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final EntityView view = loader.getController();

            view.root = root;
            view.setTarget(target);
            view.container = tab;

            // Mouse over tooltip
            final double maxWidth = 360;
            final ImageView toolTipView
                    = new ImageView(view.entityDisplayView.getImage());
            toolTipView.setSmooth(false);
            target.getEntityDescription().ifPresent((s) -> {
                final Tooltip tip = new Tooltip(s);
                tip.setShowDuration(Duration.INDEFINITE);
                tip.setGraphic(view.entityDisplayView);
                tip.setGraphic(toolTipView);
                tip.setWrapText(true);
                tip.setMaxWidth(maxWidth);
                tip.getStyleClass().addAll(
                        "DefaultText",
                        "AltFontB"
                );
                Tooltip.install(view.root, tip);
            });

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

    /**
     * Drag drop event handler used create new instances of the target entity
     * at a specific position in the parents, parents display grid.
     *
     * @param e The mouse event that was triggered.
     */
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

    /**
     * Loads an instance builder which allows you to set specific elements
     * and forms to obtain field data for the target entity that can be
     * saved to the editors display grid.
     */
    @FXML
    private void onInstanceBuilderClicked() {
        final Stage s = new Stage();
        s.initModality(Modality.APPLICATION_MODAL);
        final TypeConstructionForm form = TypeConstructionForm.init(
                s,
                this.target.getWritableFieldTypeMap()
        );

        s.showAndWait();

        if (form.isNaturalExit()) {
            try {
                final Entity e = this.target.constructEntity(form.parseTypes());
                this.container.addEntityToScene(e);

                // Case for bad form data
            } catch (final Exception e) {
                final Alert ae = new Alert(Alert.AlertType.WARNING);
                ae.setTitle("Entity Construction Failed!");
                ae.setContentText(String.format(
                        "Could not construct %s as one or more of the "
                                + "provided parameters was invalid!",
                        this.getTarget().getTargetClass().getSimpleName()
                ));
                ae.showAndWait();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Data set and collection methods
    ///////////////////////////////////////////////////////////////////////////

    /**
     * @return The root node of this scene.
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * Sets the target Entity type to the provided target.
     *
     * @param target The new target entity for this view.
     */
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

    /**
     * @return The target entity for this view.
     */
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
