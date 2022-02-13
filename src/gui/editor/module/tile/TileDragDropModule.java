package gui.editor.module.tile;

import game.tile.loader.TileRegex;
import gui.editor.LevelEditor;
import gui.editor.module.dependant.LevelEditorModule;
import gui.editor.module.dependant.CustomEventDataMap;
import gui.editor.module.tile.single.SingleTileView;
import javafx.scene.input.DragEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Java class created on 11/02/2022 for usage in project RatGame-A2. Tile
 * Drag drop module which consists mainly of the Tile drag drop operations
 * and how it interacts with a target {@link LevelEditor}.
 *
 * @author -Ry, Morgan Gardner
 * @version 0.1
 * Copyright: N/A
 */
public class TileDragDropModule implements LevelEditorModule {

    /**
     * A map consisting of unique Object identifier strings and the actual
     * object instance.
     * <p>
     * I.e., String = instance.toString(); map.get(String) = instance;
     */
    private final Map<String, SingleTileView> tileViews = new HashMap<>();

    /**
     * The level editor that this module interacts with.
     */
    private LevelEditor editor;

    /**
     * Constructs the Tile drag drop module.
     */
    public TileDragDropModule() {
        for (TileRegex r : TileRegex.values()) {
            final SingleTileView view = SingleTileView.init(
                    r.getTileFactory(),
                    r.getTargetClass(),
                    r.getAvailableSprites()
            );

            tileViews.put(view.toString(), view);
        }
    }

    /**
     * Loads the module into the level editor scene. So that it can be
     * interacted with. alongside registering even handles as well.
     *
     * @param editor The editor to load into.
     */
    @Override
    public void loadIntoScene(final LevelEditor editor) {
        this.editor = editor;

        // todo can be lambda: (str, view) -> { foo }
        this.tileViews.forEach(new BiConsumer<String, SingleTileView>() {
            @Override
            public void accept(String s, SingleTileView view) {
                editor.getTilesHBox().getChildren().add(view.getRoot());
            }
        });

        // Register this controller as a drag result handler for the Tiles
        editor.addEventHandle(
                SingleTileView.EVENT_ROUTE_ID,
                this::dragResultHandle
        );
    }

    /**
     * Handles a drag event that occurred in a level editor.
     *
     * @param editor The editor that detected the drag event.
     * @param event  The event that occurred.
     * @param row    The row position in the grid map of this events final
     *               mouse position.
     * @param col    The col position in the grid map of this events final mouse
     *               position.
     */
    private void dragResultHandle(final LevelEditor editor,
                                  final DragEvent event,
                                  final int row,
                                  final int col) {
        // todo clean up if you want; could also remove the event handles if
        //  not needed.

        final String s = SingleTileView.EVENT_SUFFIX;
        final String name = (String) event.getDragboard().getContent(
                CustomEventDataMap.CONTENT
        );

        final SingleTileView view = this.tileViews.get(name);

    }
}
