package gui.editor.module.tab.entities.view.existing;

import game.classinfo.entity.EntityInfo;
import game.classinfo.entity.MalformedWritableClassException;
import game.entity.Entity;
import game.tile.Tile;
import gui.editor.module.grid.tileview.GridUpdateListener;
import gui.editor.module.tab.entities.EntitiesTab;
import gui.type.TypeConstructionForm;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;

/**
 * Java class created on 13/02/2022 for usage in project RatGame-A2. Consists
 * of wrapping an Entity instance displaying visually some information about
 * the entity with control options for editing the state of the entity or
 * deleting it entirely.
 *
 * @author -Ry
 * @version 0.4
 * Copyright:N/A
 */
public class ExistingEntityView implements GridUpdateListener<Tile> {

    /**
     * Scene fxml resource.
     */
    private static final URL SCENE_FXML = ExistingEntityView.class.getResource(
            "ExistingEntityView.fxml"
    );

    /**
     * Root node of the scene.
     */
    private Parent root;

    /**
     * Tab/parent of the root of this scene which initialised this scene.
     */
    private EntitiesTab container;

    /**
     * The entity instance that this visualises.
     */
    private Entity entity;

    /**
     * Class info object that allows annotation parsing.
     */
    private EntityInfo<?> info;

    /**
     * Image view for the entity display sprite.
     */
    @FXML
    private ImageView entityDisplaySprite;

    /**
     * The name of the entity.
     */
    @FXML
    private Label entityNameLabel;

    /**
     * Label consisting of the Row and column position of the target entity.
     */
    @FXML
    private Label rowColLabel;

    /**
     * Static construction mechanism for loading up the required FXML whilst
     * still parameterising the object.
     *
     * @param e The entity instance that this will display.
     * @param tab The tab/parent/container that this view reports to.
     * @return Newly constructed instance.
     */
    public static ExistingEntityView init(final Entity e,
                                          final EntitiesTab tab) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final ExistingEntityView view = loader.getController();

            view.root = root;
            view.setEntity(e);
            view.container = tab;
            tab.getEditor().getTileViewModule().addTileUpdateListener(view);
            return view;


        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    /**
     * Initialises the type construction form for the target entity allowing
     * precise control over the attributes held within.
     */
    @FXML
    private void onEditClicked() {
        final Stage s = new Stage();
        s.initModality(Modality.APPLICATION_MODAL);
        final TypeConstructionForm form = TypeConstructionForm.init(
                s,
                this.info.getWritableFieldTypeMap()
        );
        try {
            form.initDefaults(this.entity);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        s.showAndWait();

        if (form.isNaturalExit()) {
            try {

                final Entity newEntity
                        = this.info.constructEntity(form.parseTypes());

                // Delete old entity
                this.container.removeExistingEntity(this.entity);

                // Call to update the entity info
                this.entity = newEntity;
                this.container.addEntityToScene(this.entity);
                update();

                // Case for bad form data
            } catch (final Exception e) {
                final Alert ae = new Alert(Alert.AlertType.WARNING);
                ae.setTitle("Entity Construction Failed!");
                ae.setContentText(String.format(
                        "Could not construct %s as one or more of the "
                                + "provided parameters was invalid!",
                        this.info.getTargetClass().getSimpleName()
                ));
                ae.showAndWait();
            }
        }
    }

    /**
     * Removes this view from the parent effectively deleting the entity.
     */
    @FXML
    private void onDeleteClicked() {
        this.container.removeExistingEntity(entity);
    }

    /**
     * Updates the visual information of the target entity.
     */
    private void update() {
        final Entity e = this.entity;

        this.entityDisplaySprite.setImage(new Image(
                e.getDisplaySprite().toExternalForm()
        ));

        // Set entity name
        this.entityNameLabel.setText(e.getClass().getSimpleName());

        // Set row  col label
        this.rowColLabel.setText(String.format(
                "(%s, %s) :: (%s)",
                entity.getRow(),
                entity.getCol(),
                entity.getHealth()
        ));
    }

    /**
     * @return Root node of this scene.
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * @return The entity instance that this view is displaying.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Set the entity for this view to the provided entity.
     * @param e The new entity for this view.
     */
    public void setEntity(final Entity e) {
        this.entity = e;
        update();

        try {
            this.info = new EntityInfo<>(e.getClass());

        } catch (final MalformedWritableClassException ex) {
            // This would disable some functionalities for the view such as Edit
        }
    }

    /**
     * Called whenever an update to some part of the grid has occurred.
     *
     * @param row  The row position in the grid which was updated.
     * @param col  The column position in the grid that was updated.
     * @param elem The element at the position in the grid that was updated.
     */
    @Override
    public void update(final int row,
                       final int col,
                       final Tile elem) {
        if (row == entity.getRow()
                && col == entity.getCol()
                && info.isBlacklistedTile(elem.getClass())) {
            this.container.removeExistingEntity(entity);
        }
    }
}
