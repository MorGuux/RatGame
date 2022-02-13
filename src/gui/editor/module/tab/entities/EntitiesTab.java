package gui.editor.module.tab.entities;

import game.classinfo.entity.EntityInfo;
import game.classinfo.entity.MalformedWritableClassException;
import game.entity.Entity;
import game.entity.loader.EntityLoader;
import gui.editor.module.grid.entityview.EntityViewModule;
import gui.editor.module.tab.entities.view.drag.EntityView;
import gui.editor.module.tab.entities.view.existing.ExistingEntityView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;
import util.SceneUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Java class created on 13/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class EntitiesTab implements Initializable {

    private static final URL SCENE_FXML
            = EntitiesTab.class.getResource("EntitiesTab.fxml");

    @FXML
    private VBox hostileEntityVbox;

    @FXML
    private VBox friendlyEntityVbox;

    @FXML
    private VBox existingEntitiesVBox;

    private Parent root;

    private final Map<String, EntityView> entityViewMap
            = Collections.synchronizedMap(new HashMap<>());

    public static EntitiesTab init() {
        final FXMLLoader loader = new FXMLLoader(SCENE_FXML);

        try {
            final Parent root = loader.load();
            final EntitiesTab tab = loader.getController();
            tab.root = root;

            return tab;
            // rethrow
        } catch (IOException e) {
            e.printStackTrace();
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void initialize(final URL url,
                           final ResourceBundle bundle) {
        for (final EntityLoader.ConstructableEntity e :
                EntityLoader.ConstructableEntity.values()) {

            try {
                final EntityInfo<?> info = new EntityInfo<>(e.getTarget());
                final EntityView view = EntityView.init(info);
                this.entityViewMap.put(view.toString(), view);

                if (info.isHostile()) {
                    this.hostileEntityVbox.getChildren().add(view.getRoot());
                } else {
                    this.friendlyEntityVbox.getChildren().add(view.getRoot());
                }

                // Stack trace, error message, then skip this entry
            } catch (final MalformedWritableClassException ex) {
                ex.printStackTrace();
                System.err.printf(
                        "Entity target class: [%s] is malformed and currently"
                                + " incapable of being loaded in its current "
                                + "state.%n",
                        e.getTarget().getName()
                );
            }
        }
    }

    public Parent getRoot() {
        return root;
    }

    public void addExistingEntity(final Entity existing) {
        this.existingEntitiesVBox.getChildren().add(
                ExistingEntityView
                        .init(existing)
                        .getRoot()
        );
    }
}
