package gui.editor.module.grid.entityview;

import game.entity.Entity;
import game.level.reader.module.GameProperties;
import game.tile.Tile;
import gui.editor.LevelEditor;
import gui.editor.module.dependant.LevelEditorModule;
import gui.game.dependant.entitymap.EntityMap;
import gui.game.dependant.tilemap.Coordinates;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import util.SceneUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Java class created on 13/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class EntityViewModule implements LevelEditorModule {

    /**
     * The level editor that this module is a member of.
     */
    private LevelEditor editor;

    /**
     * The underlying entity display map.
     */
    private EntityMap entityDisplayMap;

    /**
     * Entity ID Map, consisting of all the literal entity references held in
     * the module.
     */
    private final Map<Long, Entity> entityIDMap
            = Collections.synchronizedMap(new HashMap<>());

    /**
     * The number of rows in the entity display map, not final since we may
     * want to change it.
     */
    private int numRows;

    /**
     * The number of columns in the entity map. Not final since we may want
     * to change it.
     */
    private int numCols;

    /**
     * Loads the module into the level editor scene. So that it can be
     * interacted with.
     * <p>
     * Note that you should store the provided {@link LevelEditor} locally in
     * your class then use that when interacting with something in the editor.
     *
     * @param editor The editor to load into.
     */
    @Override
    public void loadIntoScene(final LevelEditor editor) {
        this.editor = editor;

        final GameProperties p
                = editor.getFileToEdit().getDefaultProperties();
        this.numRows = p.getRows();
        this.numCols = p.getColumns();
        entityDisplayMap = new EntityMap(
                numRows,
                numCols
        );
        entityDisplayMap.setFixedTileSize(Tile.DEFAULT_SIZE);

        editor.getFileToEdit().getEntityPositionMap().forEach((e, pos) -> {
            final ImageView view = getImageView(e);
            this.entityIDMap.put(e.getEntityID(), e);

            // Origin tile
            this.entityDisplayMap.addView(
                    e.getEntityID(),
                    view,
                    e.getRow(),
                    e.getCol()
            );

            // Occupied tiles
            for (Coordinates<Integer> i : pos) {
                this.entityDisplayMap.occupyPosition(
                        e.getEntityID(),
                        view,
                        i.getRow(),
                        i.getCol()
                );
            }
        });

        editor.getGameObjectEditorViewStackPane().getChildren().add(
                entityDisplayMap.getRoot()
        );
    }

    /**
     * Constructs an entity view for the target entity.
     *
     * @param e The entity to create a view for.
     * @return The specified entity view.
     */
    private ImageView getImageView(final Entity e) {
        final ImageView base = getImageView();
        final Image i = new Image(e.getDisplaySprite().toExternalForm());
        base.setImage(i);
        return base;
    }

    /**
     * Creates a default empty image view.
     *
     * @return Empty view.
     */
    private ImageView getImageView() {
        final ImageView view = new ImageView();
        view.setFitWidth(Tile.DEFAULT_SIZE);
        view.setFitWidth(Tile.DEFAULT_SIZE);
        view.setPreserveRatio(false);
        view.setSmooth(false);

        return view;
    }

    /**
     * @return The number of rows in the entity map.
     */
    public int getNumRows() {
        return numRows;
    }

    /**
     * @return The number of columns in the entity map.
     */
    public int getNumCols() {
        return numCols;
    }

    /**
     * Gets the entity with the specified id.
     *
     * @param id The id of the entity.
     * @return The entity if exists, else null.
     */
    public Entity getEntityForID(final long id) {
        return this.entityIDMap.get(id);
    }

    /**
     * Gets all display views for the entity with the target ID.
     *
     * @param id The entity id to get the display views for.
     * @return All display views, Origin tile, and occupied tiles.
     */
    public ImageView[] getDisplayViewsForID(final long id) {
        final ImageView origin = this.entityDisplayMap.getOriginView(id);
        final ImageView[] occupied = this.entityDisplayMap.getOccupiedView(id);

        final List<ImageView> views = new ArrayList<>();

        // Get display references
        if (origin != null) {
            views.add(origin);
        }
        if (occupied != null) {
            views.addAll(List.of(occupied));
        }

        return views.toArray(new ImageView[0]);
    }

    /**
     * @return All entities that currently exist in the game map.
     */
    public Entity[] getAllEntities() {
        return new LinkedList<>(
                this.entityIDMap.values()).toArray(new Entity[0]
        );
    }

    /**
     * Removes the entity with the specified ID from the view, and module.
     *
     * @param id The entity to remove.
     */
    public void deleteEntityByID(final long id) {
        this.entityDisplayMap.removeView(id, true);
        this.entityIDMap.remove(id);
    }

    /**
     * Updates the display sprites for all views of the target entity.
     *
     * @param id The entity ID obtained through {@link Entity#getEntityID()}.
     */
    public void updateEntity(final long id) {
        final ImageView[] views = getDisplayViewsForID(id);
        final Image display = new Image(
                getEntityForID(id).getDisplaySprite().toExternalForm()
        );

        for (final ImageView view : views) {
            view.setImage(display);
        }
    }

    /**
     * Adds the provided entity to the scene.
     *
     * @param entity The entity to display.
     */
    public void addEntity(final Entity entity) {
        final ImageView display = getImageView(entity);
        this.entityIDMap.put(entity.getEntityID(), entity);

        SceneUtil.scaleNodeIn(display);
        this.entityDisplayMap.addView(
                entity.getEntityID(),
                display,
                entity.getRow(),
                entity.getCol()
        );
    }
}
