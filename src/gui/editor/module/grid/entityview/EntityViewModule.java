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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Java class created on 13/02/2022 for usage in project RatGame-A2.
 *
 * @author -Ry
 */
public class EntityViewModule implements LevelEditorModule {

    private LevelEditor editor;
    private EntityMap entityDisplayMap;
    private final Map<Long, Entity> entityIDMap
            = Collections.synchronizedMap(new HashMap<>());
    private int numRows;
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

    private ImageView getImageView(final Entity e) {
        final ImageView base = getImageView();
        final Image i = new Image(e.getDisplaySprite().toExternalForm());
        base.setImage(i);
        return base;
    }

    private ImageView getImageView() {
        final ImageView view = new ImageView();
        view.setFitWidth(Tile.DEFAULT_SIZE);
        view.setFitWidth(Tile.DEFAULT_SIZE);
        view.setPreserveRatio(false);
        view.setSmooth(false);

        return view;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumCols() {
        return numCols;
    }

    public Entity getEntityForID(final long id) {
        return this.entityIDMap.get(id);
    }

    public ImageView[] getDisplayViewsForID(final long id) {
        final ImageView origin = this.entityDisplayMap.getOriginView(id);
        final ImageView[] occupied = this.entityDisplayMap.getOccupiedView(id);

        List<ImageView> views = new ArrayList<>();

        if (origin != null) {
            views.add(origin);
        }

        if (occupied != null) {
            views.addAll(List.of(occupied));
        }

        return views.toArray(new ImageView[0]);
    }
}
