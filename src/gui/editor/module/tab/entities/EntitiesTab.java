package gui.editor.module.tab.entities;

import game.classinfo.entity.EntityInfo;
import game.classinfo.entity.MalformedWritableClassException;
import game.entity.Entity;
import game.entity.loader.EntityLoader;
import game.tile.Tile;
import gui.editor.LevelEditor;
import gui.editor.module.dependant.CustomEventDataMap;
import gui.editor.module.dependant.LevelEditorDragHandler;
import gui.editor.module.grid.entityview.EntityViewModule;
import gui.editor.module.tab.TabModuleContent;
import gui.editor.module.tab.TabModules;
import gui.editor.module.tab.entities.view.drag.EntityView;
import gui.editor.module.tab.entities.view.existing.ExistingEntityView;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
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
 * Java class created on 13/02/2022 for usage in project RatGame-A2. Consists
 * of all the Entities within a specific level providing methods to edit said
 * entities.
 *
 * @author -Ry
 * @version 0.2
 */
public class EntitiesTab implements
        Initializable,
        TabModuleContent,
        LevelEditorDragHandler {

    /**
     * Scene fxml resource.
     */
    private static final URL SCENE_FXML
            = EntitiesTab.class.getResource("EntitiesTab.fxml");

    ///////////////////////////////////////////////////////////////////////////
    // FXML attributes
    ///////////////////////////////////////////////////////////////////////////

    /**
     * VBox containing the hostile entities that can be drag-dropped.
     */
    @FXML
    private VBox hostileEntityVbox;

    /**
     * VBox containing the friendly entities that can be drag-dropped.
     */
    @FXML
    private VBox friendlyEntityVbox;

    /**
     * VBox consisting of all the entities currently in the level.
     */
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

    /**
     * Map which is used for Entity drag and drop handling, mapping some
     * {@link CustomEventDataMap#CONTENT_ID} for an EntityView to its literal
     * entity view.
     */
    private final Map<String, EntityView> entityViewMap
            = Collections.synchronizedMap(new HashMap<>());

    /**
     * Map which is used for linking some Entity to its visual representation
     * in the existing entities VBox.
     */
    private final Map<Entity, ExistingEntityView> existingEntityViewMap
            = Collections.synchronizedMap(new HashMap<>());

    /**
     * Static construction mechanism for loading the Entities tab. This only
     * loads the required dependencies and does not load a target editor.
     *
     * @return Newly constructed and setup tab ready to be populated with data.
     */
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
    // Event handlers
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Handles a drag event that occurred in a level editor.
     *
     * @param editor The editor that detected the drag event.
     * @param event  The event that occurred.
     * @param row    The row position in the grid map of this events final
     *               mouse position.
     * @param col    The col position in the grid map of this events final mouse
     */
    @Override
    public void handle(final LevelEditor editor,
                       final DragEvent event,
                       final int row,
                       final int col) {
        final DataFormat content = CustomEventDataMap.CONTENT;
        final EntityView view = this.entityViewMap.get(
                (String) event.getDragboard().getContent(content)
        );

        final Class<? extends Tile> tile =
                this.editor.getTileViewModule().getTileAt(row, col).getClass();
        if (!view.getTarget().isBlacklistedTile(tile)) {
            final Entity e = view.newInstance(row, col);

            // Debug string
            System.out.printf(
                    "[ENTITY-CREATE] :: [%s]%n",
                    e.toString()
            );

            this.addEntityToScene(e);

            // Entity doesn't belong on that tile
        } else {
            final Alert ae = new Alert(Alert.AlertType.WARNING);
            ae.setHeaderText("Entity doesn't belong here!");
            ae.setContentText(String.format(
                    "%s does not belong on %s tiles!!!",
                    view.getTarget().getTargetClass().getSimpleName(),
                    tile.getSimpleName()
            ));
            ae.showAndWait();
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

        // Register this class as a drag drop event handler; specifically,
        // the interfaced method: 'handle'
        this.editor.addEventHandle(EntityView.DRAG_DROP_EVENT_ID, this);

        this.editor.getFileToEdit().getEntityPositionMap().forEach((e, pos) -> {
            this.addExistingEntity(e);
        });
        this.editor.getEntitiesTabBorderpane().setCenter(this.root);
    }

    /**
     * Initialiser method which loads the static data into the fields.
     *
     * @param url Resource url, unused.
     * @param bundle Un-used.
     */
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

    /**
     * @return The root node of the entity tab.
     */
    public Parent getRoot() {
        return root;
    }

    /**
     * @return The editor that the tab is visualising and allowing data
     * modifications for.
     */
    public LevelEditor getEditor() {
        return editor;
    }

    /**
     * @return Context used to display the entities held here in the game grid.
     */
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
     * @return {@code true} if the specified entity has been added to the
     * scene. Else if not then {@code false} is returned.
     */
    public boolean addEntityToScene(final Entity entity) {

        final int row = entity.getRow();
        final int maxRow = this.editor.getRows();
        final int col = entity.getCol();
        final int maxCol = this.editor.getCols();

        if ((row >= 0)
                && (row < maxRow)
                && (col >= 0)
                && (col < maxCol)) {
            final ExistingEntityView v = ExistingEntityView.init(entity, this);
            this.existingEntityViewMap.put(entity, v);

            SceneUtil.scaleNodeIn(v.getRoot());
            this.existingEntitiesVBox.getChildren().add(v.getRoot());

            getDisplayContext().addEntity(entity);
            return true;
        } else {
            return false;
        }
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
