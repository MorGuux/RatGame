package gui.editor.module.tab.entities.view.existing;

import game.classinfo.entity.EntityInfo;
import game.classinfo.entity.MalformedWritableClassException;
import game.entity.Entity;
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
 * Java class created on 13/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class ExistingEntityView {

    private static final URL SCENE_FXML = ExistingEntityView.class.getResource(
            "ExistingEntityView.fxml"
    );

    private Parent root;
    private EntitiesTab container;
    private Entity entity;
    private EntityInfo<?> info;

    @FXML
    private ImageView entityDisplaySprite;
    @FXML
    private Label entityNameLabel;
    @FXML
    private Label rowColLabel;

    public static ExistingEntityView init(final Entity e,
                                          final EntitiesTab tab) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final ExistingEntityView view = loader.getController();

            view.root = root;
            view.setEntity(e);
            view.container = tab;
            return view;


        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

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

    @FXML
    private void onDeleteClicked() {
        this.container.removeExistingEntity(entity);
    }

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

    public Parent getRoot() {
        return root;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(final Entity e) {
        this.entity = e;
        update();

        try {
            this.info = new EntityInfo<>(e.getClass());

        } catch (MalformedWritableClassException ex) {
            // todo disable edit button
        }
    }
}
