package gui.editor.module.tab.entities;

import game.classinfo.entity.EntityInfo;
import game.classinfo.entity.MalformedWritableClassException;
import game.entity.Entity;
import game.entity.loader.EntityLoader;
import gui.editor.LevelEditor;
import gui.editor.module.grid.entityview.EntityViewModule;
import gui.editor.module.tab.TabModuleContent;
import gui.editor.module.tab.TabModules;
import gui.editor.module.tab.entities.view.drag.EntityView;
import gui.editor.module.tab.entities.view.existing.ExistingEntityView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

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
public class EntitiesTab implements Initializable, TabModuleContent {

    private static final URL SCENE_FXML
            = EntitiesTab.class.getResource("EntitiesTab.fxml");

    ///////////////////////////////////////////////////////////////////////////
    // FXML attributes
    ///////////////////////////////////////////////////////////////////////////

    @FXML
    private VBox hostileEntityVbox;

    @FXML
    private VBox friendlyEntityVbox;

    @FXML
    private VBox existingEntitiesVBox;

    ///////////////////////////////////////////////////////////////////////////
    // Class attributes
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The root node of the object hierarchy.
     */
    private Parent root;

    /**
     * The container module of this tab.
     */
    private TabModules module;

    /**
     * The editor that this tab is a member of.
     */
    private LevelEditor editor;

    private final Map<String, EntityView> entityViewMap
            = Collections.synchronizedMap(new HashMap<>());

    private final Map<Entity, ExistingEntityView> existingEntityViewMap
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

    ///////////////////////////////////////////////////////////////////////////
    // Initialisers
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Called when this tab is loaded by the container module, when said
     * container module is in a state to do so.
     *
     * @param editor    The editor that the module is a part of.
     * @param container The literal container tab of this content.
     */
    @Override
    public void loadIntoScene(final LevelEditor editor,
                              final TabModules container) {
        this.module = container;
        this.editor = editor;

        this.editor.getFileToEdit().getEntityPositionMap().forEach((e, pos) -> {
            this.addExistingEntity(e);
        });
        this.editor.getEntitiesTabBorderpane().setCenter(this.root);
    }

    @Override
    public void initialize(final URL url,
                           final ResourceBundle bundle) {
        // Load in for all entities, their respective class type drag drop
        // systems.
        for (final EntityLoader.ConstructableEntity e
                : EntityLoader.ConstructableEntity.values()) {

            try {
                final EntityInfo<?> info = new EntityInfo<>(e.getTarget());
                final EntityView view = EntityView.init(info, this);
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

    ///////////////////////////////////////////////////////////////////////////
    // Getters and setters
    ///////////////////////////////////////////////////////////////////////////

    public Parent getRoot() {
        return root;
    }

    public LevelEditor getEditor() {
        return editor;
    }

    public EntityViewModule getDisplayContext() {
        return this.editor.getEntityViewModule();
    }

    /**
     * Adds only to this scene the provided entity.
     *
     * @param existing The entity to add.
     */
    private void addExistingEntity(final Entity existing) {
        final ExistingEntityView v = ExistingEntityView.init(existing, this);
        this.existingEntityViewMap.put(existing, v);
        this.existingEntitiesVBox.getChildren().add(v.getRoot());
    }

    /**
     * Adds the provided entity to the scenes managed by this tab.
     *
     * @param entity The entity to add.
     */
    public void addEntityToScene(final Entity entity) {
        final ExistingEntityView v = ExistingEntityView.init(entity, this);
        this.existingEntityViewMap.put(entity, v);
        this.existingEntitiesVBox.getChildren().add(v.getRoot());
        getDisplayContext().addEntity(entity);
    }

    /**
     * Removes from the provided entity from all scenes it is present in.
     *
     * @param entity The entity to remove.
     * @throws NullPointerException If the target entity is not a member of
     *                              the any scenes managed by this tab.
     */
    public void removeExistingEntity(final Entity entity) {
        final ExistingEntityView view = this.existingEntityViewMap.get(entity);

        // Remove from display
        this.existingEntityViewMap.remove(entity);
        this.existingEntitiesVBox.getChildren().remove(view.getRoot());

        // Remove the entity visually
        getDisplayContext().deleteEntityByID(
                entity.getEntityID()
        );
    }
}
