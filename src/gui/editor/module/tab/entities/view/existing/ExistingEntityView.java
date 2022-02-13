package gui.editor.module.tab.entities.view.existing;

import game.classinfo.entity.EntityInfo;
import game.classinfo.entity.MalformedWritableClassException;
import game.entity.Entity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    private Entity entity;
    private EntityInfo<?> info;

    @FXML
    private ImageView entityDisplaySprite;
    @FXML
    private Label entityNameLabel;
    @FXML
    private Label rowColLabel;

    public static ExistingEntityView init(final Entity e) {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final ExistingEntityView view = loader.getController();

            view.root = root;
            view.setEntity(e);
            return view;


        } catch (final IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @FXML
    private void onEditClicked() {
    }

    @FXML
    private void onDeleteClicked() {
    }

    public Parent getRoot() {
        return root;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(final Entity e) {
        this.entity = e;
        this.entityDisplaySprite.setImage(new Image(
                e.getDisplaySprite().toExternalForm()
        ));

        // Set entity name
        this.entityNameLabel.setText(e.getClass().getSimpleName());

        // Set row  col label
        this.rowColLabel.setText(String.format(
                "(%s, %s) :: (%s))",
                entity.getRow(),
                entity.getCol(),
                entity.getHealth()
        ));

        try {
            this.info = new EntityInfo<>(e.getClass());

        } catch (MalformedWritableClassException ex) {
            // todo disable edit button
        }
    }
}
